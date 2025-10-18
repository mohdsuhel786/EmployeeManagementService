pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        ECR_ACCOUNT = '123456789012'
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
                        aws ecr get-login-password --region ${AWS_REGION} | \
                        docker login --username AWS --password-stdin ${ECR_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com
                    """
                }
            }
        }

        stage('Detect Changed Services') {
            steps {
                script {
                    changedServices = []

                    for (svc in SERVICES) {
                        def result = sh(script: "git diff --name-only HEAD~1 HEAD | grep '^${BASE_PATH}/${svc}/'", returnStatus: true)
                        if (result == 0) {
                            changedServices.add(svc)
                        }
                    }

                    if (changedServices.size() == 0) {
                        echo "No services changed. Skipping build."
                        currentBuild.result = 'SUCCESS'
                        return
                    } else {
                        echo "Services to build: ${changedServices}"
                    }
                }
            }
        }

        stage('Build and Push Docker Images') {
            when {
                expression { changedServices.size() > 0 }
            }
            steps {
                script {
                    def builds = changedServices.collectEntries { svc ->
                        [(svc): {
                            dir("${WORKSPACE}/${BASE_PATH}/${svc}") {
                                sh """
                                    mvn clean package -DskipTests jib:build \
                                    -Djib.to.image=${ECR_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com/${svc}:${GIT_COMMIT}
                                """
                            }
                        }]
                    }
                    parallel builds
                }
            }
        }

        stage('Deploy Updated Services to EKS') {
            when {
                expression { changedServices.size() > 0 }
            }
            steps {
                script {
                    // Loop through changed services and apply their respective YAML files
                    for (svc in changedServices) {
                        sh "kubectl apply -f ${WORKSPACE}/${BASE_PATH}/k8s/descriptive/${svc}-deployment.yaml"
                    }
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
