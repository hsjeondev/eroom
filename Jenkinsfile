pipeline {
    agent any

    stages {
        stage('Build & Deploy') {
            when {
                branch 'develop'
            }
            steps {
                echo '배포: develop 서버로'
                sh './springboot-docker/deploy-dev.sh'    // ✅ 경로 수정!
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
