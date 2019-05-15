#!groovy
build('adapter-bank-spring-boot-starter', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    def javaLibPipeline
    runStage('load JavaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLib.groovy")
    }

    def buildImageTag = "2a8b44ac628c1bdb729abd04ed7a2a54676e574b"
    javaLibPipeline(buildImageTag)
}