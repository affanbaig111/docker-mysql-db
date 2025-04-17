pipeline {
    agent any

    tools {
        maven 'maven-tool'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-creds')
    }

   triggers {
           // Auto-trigger on PR events (requires GitHub Plugin)
           GitHubPRTrigger(
               cron: '',  // Leave empty for event-driven triggering
               triggerPhrase: '.*', // Optional: regex to match PR comments
               onlyTriggerPhrase: false,
               useGitHubHooks: true, // Let Jenkins auto-create webhooks
               permitAll: true,
               autoCloseFailedPullRequests: false,
               adminlist: 'your-github-username' // GitHub admin(s)
           )
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
                sh 'docker system prune -af || true'
                sh 'docker volume prune -f || true'
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
