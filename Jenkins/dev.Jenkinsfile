def services = ['service-registry', 'api-gateway', 'auth-service', 'user-service', 'employee-service']
def basePath = 'D:/Workbench/micrservice-working/EmployeeManagementSystem/EmployeeManagementSystem'

pipeline {
    agent any
    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }
    stages {
        stage('Build Images') {
            steps {
                script {
                    // create a map of parallel stages dynamically
                    def builds = services.collectEntries { svc ->
                        [(svc): {
                            dir("${basePath}/${svc}") {
                                bat 'mvn -T 1C clean package -DskipTests jib:dockerBuild'
                            }
                        }]
                    }
                    parallel builds   // run them all at once
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
               bat "kubectl apply -f ${basePath}/k8s/descriptive/k8s-all-services.yaml"
            }
        }
    }
      post {
            always {
                cleanWs()
            }
        }
}
