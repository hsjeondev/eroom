pipeline {
    agent any

    stages {
        stage('Build & Deploy') {
            when {
                branch 'develop'
            }
            steps {
                echo '배포: develop 서버로'

                sh 'cp /mnt/env/.env ./springboot-docker/.env'

                sh './springboot-docker/deploy-dev.sh'
            }
        }

        stage('Build & Deploy Prod') {
            when {
                branch 'master'
            }
            steps {
                echo '배포: production 서버로'
                sh './deploy-prod.sh'
            }
        }
    }
}
