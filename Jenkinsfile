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
        // Leave empty for Docker Desktop local images
        DOCKER_REGISTRY = ''
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: "${params.BRANCH}", url: "${REPO_URL}"
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    changedServices = []

                    for (svc in services) {
                        // Check if any file changed in the service folder
                        def result = bat(
                            script: "git diff --name-only HEAD~1 HEAD | findstr /R /C:\"^${basePath.replaceAll('\\\\','/').replaceAll(':','')}/${svc}/\"",
                            returnStatus: true
                        )
                        if (result == 0) {
                            changedServices.add(svc)
                        }
                    }

                    if (changedServices.size() == 0) {
                        echo "No services changed. Building all services for local environment."
                        changedServices = services
                    } else {
                        echo "Services to build: ${changedServices}"
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    def builds = changedServices.collectEntries { svc ->
                        [(svc): {
                            dir("${basePath}/${svc}") {
                                echo "Building Docker image for ${svc}"
                                bat "mvn -T 1C clean package -DskipTests jib:dockerBuild -Djib.to.image=${svc}:latest"
                            }
                        }]
                    }
                    parallel builds
                }
            }
        }

        stage('Push Images (optional)') {
            when { expression { env.DOCKER_REGISTRY && env.DOCKER_REGISTRY != '' } }
            steps {
                script {
                    changedServices.each { svc ->
                        bat "docker tag ${svc}:latest ${DOCKER_REGISTRY}/${svc}:latest"
                        bat "docker push ${DOCKER_REGISTRY}/${svc}:latest"
                    }
                }
            }
        }

//         stage('Trigger ArgoCD Sync') {
//             steps {
//                 script {
//                     // ArgoCD CLI must be installed and logged in
//                     def appName = params.ENV == 'prod' ? 'employee-management-prod' : 'employee-management-dev'
//                     bat "argocd app sync ${appName} --grpc-web"
//                     echo "Triggered ArgoCD sync for ${appName}"
//                 }
//             }
//         }

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
