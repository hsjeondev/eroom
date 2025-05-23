pipeline {
    agent any

    stages {
        stage('Build & Deploy') {
            when {
                expression {
                    return env.BRANCH_NAME == 'develop'
                }
            }
            steps {
                echo '배포: develop 서버로 고고'

                sh './springboot-docker/deploy-dev.sh'
            }
        }

        stage('Build & Deploy Prod') {
            when {
                expression {
                    return env.BRANCH_NAME == 'master'
                }
            }
            steps {
                echo '배포: production 서버로'
                sh './deploy-prod.sh'
            }
        }
    }
}
