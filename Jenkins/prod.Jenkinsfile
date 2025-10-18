pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'                   // your region
        ECR_ACCOUNT = '123456789012'                // your AWS account ID
        ECR_REPO = 'employee-management'           // ECR repo prefix
        BASE_PATH = 'EmployeeManagementSystem'
        SERVICES = ['service-registry', 'api-gateway', 'auth-service', 'user-service', 'employee-service']
    }

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/yourorg/EmployeeManagementSystem.git'
            }
        }

        stage('Login to ECR') {
            steps {
                script {
                    sh """
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com
                    """
                }
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    def builds = SERVICES.collectEntries { svc ->
                        [(svc): {
                            dir("${WORKSPACE}/${BASE_PATH}/${svc}") {
                                sh """
                                    mvn clean package -DskipTests jib:build \
                                    -Djib.to.image=${ECR_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com/${svc}:latest
                                """
                            }
                        }]
                    }
                    parallel builds
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                script {
                    sh "kubectl apply -f ${WORKSPACE}/${BASE_PATH}/k8s/descriptive/k8s-all-services.yaml"
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
