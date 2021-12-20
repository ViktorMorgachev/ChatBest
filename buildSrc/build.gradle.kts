plugins {
  `kotlin-dsl`
}
repositories {
  google()
  mavenCentral()
  maven {
    setUrl("https://repo1.maven.org/maven2/")
    setUrl("https://mvnrepository.com")
  }
  jcenter()

}