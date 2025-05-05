pipeline {
    agent any

    

    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-creds')
        DB_USER = 'root'
        DB_PASS = 'root'
        DB_NAME = 'Book'
        DB_CONTAINER = 'mysqldb'
        SQL_FILE = 'scripts/alter_table.sql'
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scmGit(
                    branches: [[name: '*/master']],
                    extensions: [],
                    userRemoteConfigs: [[url: 'https://github.com/affanbaig111/docker-mysql-db']]
                )
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                }
            }
        }

        stage('Build with Jib') {
            steps {
                echo "Building and pushing Docker image using Jib..."
                sh 'mvn compile com.google.cloud.tools:jib-maven-plugin:3.4.0:build'
            }
        }

        stage('Clean Up Old Docker Resources') {
            steps {
                echo "Stopping and removing old containers/volumes..."
                sh 'docker-compose -f docker-compose.yml down -v --remove-orphans || true'
               
            }
        }
        stage('Execute SQL') {
            steps {
                script {
                    if (fileExists(SQL_FILE)) {
                        echo "✅ Found SQL script at ${SQL_FILE}, executing it..."

                        sh """
                        docker cp ${SQL_FILE} ${DB_CONTAINER}:/tmp/alter_table.sql
                        docker exec ${DB_CONTAINER} sh -c 'mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} < /tmp/alter_table.sql'
                        """

                    } else {
                        echo "⚠️ SQL script not found contining next stages "

                    }
                }
            }
        }

        stage('Start with Docker Compose') {
            steps {
                echo "Starting services using Docker Compose..."
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }

        stage('Run Postman Tests') {
            steps {
                echo "Running Newman tests..."
                sh 'newman run postman/collection.json'
            }
        }
    }

    post {
        always {
            echo 'Logging out from Docker...'
            sh 'docker logout'
        }
    }
}
