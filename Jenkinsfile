// Jenkinsfile for EmployeeManagementService with ArgoCD deployment

def services = ['service-registry', 'api-gateway', 'auth-service', 'user-service', 'employee-service']
def basePath = 'D:/Workbench/micrservice-working/EmployeeManagementSystem/EmployeeManagementSystem'

pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to build')
        choice(name: 'ENV', choices: ['dev', 'prod'], description: 'Environment to deploy')
    }

    environment {
        REPO_URL = 'https://github.com/mohdsuhel786/EmployeeManagementService.git'
        DOCKER_REGISTRY = '' // leave empty for Docker Desktop local images
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: "${params.BRANCH}", url: "${REPO_URL}"
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def builds = services.collectEntries { svc ->
                        [(svc): {
                            dir("${basePath}/${svc}") {
                                echo "Building Docker image for ${svc}"
                                // Build Docker image using Jib (local Docker Desktop)
                                bat "mvn -T 1C clean package -DskipTests jib:dockerBuild -Djib.to.image=${svc}:latest"
                            }
                        }]
                    }
                    parallel builds
                }
            }
        }

        stage('Push Images (optional)') {
            when { expression { env.DOCKER_REGISTRY != '' } }
            steps {
                script {
                    services.each { svc ->
                        bat "docker tag ${svc}:latest ${DOCKER_REGISTRY}/${svc}:latest"
                        bat "docker push ${DOCKER_REGISTRY}/${svc}:latest"
                    }
                }
            }
        }

        stage('Trigger ArgoCD Sync') {
            steps {
                script {
                    // Trigger ArgoCD sync via CLI
                    // Make sure ArgoCD CLI (argocd) is installed and logged in
                    def appName = params.ENV == 'prod' ? 'employee-management-prod' : 'employee-management-dev'
                    bat "argocd app sync ${appName} --grpc-web"
                    echo "Triggered ArgoCD sync for ${appName}"
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
        always {
            cleanWs(deleteDirs: true, disableDeferredWipeout: true)
        }
    }
}
