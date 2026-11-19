# iFogSim2 Research Workspace

A local research copy of [Cloudslab iFogSim](https://github.com/Cloudslab/iFogSim) used to explore resource management in distributed edge and fog environments.

## Explore this copy

Open the repository as a Java project and use the setup notes below to configure the bundled [JARs](jars). Two runnable entry points are [TranslationServiceFog_Clustering](src/org/fog/test/perfeval/TranslationServiceFog_Clustering.java) and [CrowdSensing_Microservices_RandomMobility_Clustering](src/org/fog/test/perfeval/CrowdSensing_Microservices_RandomMobility_Clustering.java).

See the upstream references below for attribution and the [MIT license](LICENSE) for terms.

---

## Upstream iFogSim2 overview
A Toolkit for Modeling and Simulation of Resource Management Techniques in Internet of Things, Edge and Fog Computing Environments with the following new features:
 * Mobility-support and Migration Management
   * Supporting real mobility datasets
   * Implementing different random mobility models 
 * Microservice Orchestration
 * Dynamic Distributed Clustering
 * Any Combinations of Above-mentioned Features
 * Full Compatibility with the Latest Version of the CloudSim (i.e., [CloudSim 5](https://github.com/Cloudslab/cloudsim/releases)) and [Previous iFogSim Version](https://github.com/Cloudslab/iFogSim1) and Tutorials

iFogSim2 currently encompasses several new usecases such as:
 * Audio Translation Scenario
 * Healthcare Scenario
 * Crowd-sensing Scenario

## Upstream setup instructions
* Eclipse IDE:
  * Create a Java project
  * Inside the project directory, initialize an empty Git repository with the following command:
  ```
  git init
  ```
  * Add the Git repository of iFogSim2 as the `origin` remote:
  ```
  git remote add origin https://github.com/Cloudslab/iFogSim
  ```
  * Pull the contents of the repository to your machine:
  ```
  git pull origin main
  ```
  * Include the JARs to your project  
  * Run the example files (e.g. TranslationServiceFog_Clustering.java, CrowdSensing_Microservices_RandomMobility_Clustering.java) to get started

* IntelliJ IDEA:
  * Clone the iFogSim2 Git repository to desired folder:
  ```
  git clone https://github.com/Cloudslab/iFogSim
  ```
  * Select "project from existing resources" from the "File" drop-down menu
  * Verify the Java version
  * Verify the external libraries in the "JARs" Folder are added to the project
  * Run the example files (e.g. TranslationServiceFog_Clustering.java, CrowdSensing_Microservices_RandomMobility_Clustering.java) to get started


## References
 * Redowan Mahmud, Samodha Pallewatta, Mohammad Goudarzi, and Rajkumar Buyya, <A href="https://arxiv.org/abs/2109.05636">iFogSim2: An Extended iFogSim Simulator for Mobility, Clustering, and Microservice Management in Edge and Fog Computing Environments</A>, Journal of Systems and Software (JSS), Volume 190, Pages: 1-17, ISSN:0164-1212, Elsevier Press, Amsterdam, The Netherlands, August 2022.
 * Harshit Gupta, Amir Vahid Dastjerdi , Soumya K. Ghosh, and Rajkumar Buyya, <A href="http://www.buyya.com/papers/iFogSim.pdf">iFogSim: A Toolkit for Modeling and Simulation of Resource Management Techniques in Internet of Things, Edge and Fog Computing Environments</A>, Software: Practice and Experience (SPE), Volume 47, Issue 9, Pages: 1275-1296, ISSN: 0038-0644, Wiley Press, New York, USA, September 2017.
 * Redowan Mahmud and Rajkumar Buyya, <A href="http://www.buyya.com/papers/iFogSim-Tut.pdf">Modelling and Simulation of Fog and Edge Computing Environments using iFogSim Toolkit</A>, Fog and Edge Computing: Principles and Paradigms, R. Buyya and S. Srirama (eds), 433-466pp, ISBN: 978-111-95-2498-4, Wiley Press, New York, USA, January 2019.

## Java dependency setup

The IntelliJ project loads Guava and its companion failureaccess JAR from the
jars directory. Keep both on the classpath and remove any separately installed
Guava 18.0 JAR from the IDE. The bundled artifacts are from Maven Central:

- `guava-33.8.0-jre.jar` SHA-256 `97b79ec3f2c0a6313a0c2df51ce1abdf8907e6531803467b81efcaaf99b35161`
- `failureaccess-1.0.3.jar` SHA-256 `cbfc3906b19b8f55dd7cfd6dfe0aa4532e834250d7f080bd8d211a3e246b59cb`
