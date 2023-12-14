# 1. Detailed response to each task and related sub-tasks

Student ID：230109586	Student Name：Boyan Li

### Pre-Pequisites

Check the connectivity status of mobaXterm and edge VM：edge <username = student, password = CSC8110!>

Check the kubectl installation and the k8s cluster connection status to make sure the environment is ready.

```bash
kubectl version --client		# check kubectl installation
kubectl cluster-info				# check k8s cluster connection
```

![0-prep检查kubectl安装和k8s cluster的连接状态，确定环境准备就绪](/Users/boyan/Desktop/images/0-prep检查kubectl安装和k8s cluster的连接状态，确定环境准备就绪.png)

### Object 1: Deploy and access the Kubernetes Dashboard and a Web Application Component

#### KR1 · Deploy ’Kubernetes Dashboard’ on the provided VM with CLI and access/login the Dashboard.

```bash
mkdir Config												# create the folder Config
cd ~/Config													# cd the folder Config
wget https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml		# download recommended.yaml
kubectl apply -f recommonded.yaml		# deploy/update recommonded.yaml
```

![1-1-1-下载recommended文件](/Users/boyan/Desktop/images/1-1-1-下载recommended文件.png)

![1-1-2-kubectl apply recommended文件](/Users/boyan/Desktop/images/1-1-2-kubectl apply recommended文件.png)

```bash
kubectl get pods -A												# check pods status in all namespaces
kubectl get pods -n kubernetes-dashboard	# check pods status in namespace "Kubernetes-dashboard"
kubectl -n kubernetes-dashboard get svc		# check services in namespace "Kubernetes-dashboard"
kubectl proxy															# turn on the server and access the Dashboard UI
```

![1-1-3-kubectl check pods+svc](/Users/boyan/Desktop/images/1-1-3-kubectl check pods+svc.png)

After run “kubectl proxy”, check the status of pods and services again.

![1-1-4-启动proxy代理，再次检查pods svc状态](/Users/boyan/Desktop/images/1-1-4-启动proxy代理，再次检查pods svc状态.png)

Use the browser in the VM which has the mobaXterm, visit K8S-dashboard：https://192.168.0.102:30443

​	- Need to imput token. Here's a quick way to get token:

```bash
microk8s config		# get token: bjdPb2YzYmMxWFRrUHIvNHh2SlhsV3BQTjFUallHeno2ZW9tYXVid0hNOD0K
```

​	- Visit successfully:![1-1-5-access to K8s-dashboard  welcome view访问截图](/Users/boyan/Desktop/images/1-1-5-access to K8s-dashboard  welcome view访问截图.png)

#### KR2 · Deploy an instance of the Docker image "nclcloudcomputing/javabenchmarkapp" via CLI.

Create and edit "javaapp-deploy.yaml" file in ~/Config.

```bash
touch javaapp-deploy.yaml		# create javaapp-deploy.yaml
nano javaapp-deploy.yaml		# modify javaapp-deploy.yaml
```

```bash
# javaapp-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: javabenchmarkapp-deployment
spec:
  selector:
    matchLabels:
      app: javabenchmarkapp
  template:
    metadata:
      labels:
        app: javabenchmarkapp
    spec:
      containers:
      - name: javabenchmarkapp-container
        image: nclcloudcomputing/javabenchmarkapp
        resources:
          limits:
            memory: "512Mi"
            cpu: "500m"
        ports:
        - containerPort: 8080
```

```bash
kubectl apply -f javaapp-deploy.yaml		# run/update javaapp-deploy.yaml
```

Deploy successfully.

![1-2-1-部署javabenchmarkapp截图](/Users/boyan/Desktop/images/1-2-1-部署javabenchmarkapp截图.png)

#### KR3 · Deploy a NodePort service so that the web app is accessible via http://localhost:30000/primecheck. The container uses port 8080 internally.

Create and edit "javaapp-svc.yaml" file in ~/Config.

```bash
touch javaapp-svc.yaml		# create javaapp-svc.yaml
nano javaapp-svc.yaml		# modify javaapp-svc.yaml
```

```bash
# javaapp-svc.yaml
apiVersion: v1
kind: Service
metadata:
  name: javabenchmarkapp-service
spec:
  type: NodePort
  selector:
    app: javabenchmarkapp
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30000
```

```bash
kubectl apply -f javaapp-svc.yaml		# run/update javaapp-svc.yaml
```

Deploy service successfully.

![1-3-1-service 部署成功](/Users/boyan/Desktop/images/1-3-1-service 部署成功.png)

the web app is accessible via http://192.168.0.102:30000/primecheck

![1-3-2-成功访问30000:primecheck](/Users/boyan/Desktop/images/1-3-2-成功访问30000:primecheck.png)

### Object 2: Deploy the monitoring stack of Kubernetes

#### KR1 · Enable observability service from microk8s addons.

```bash
# command line
microk8s enable observability
```

​	"microk8s":  is interface of the MicroK8s tool for managing and operating MicroK8s clusters. 

​	"enable": This is a MicroK8s command that enables or activates a specific function or plug-in. In this case, enable is used to enable functionality related to observability.

​	"observability": This is the name given to a set of observability capabilities that typically include tools and components for monitoring and diagnosing Kubernetes clusters and applications. 

![2-1-1-microk8s enable observability](/Users/boyan/Desktop/images/2-1-1-microk8s enable observability.png)

: Grafana Login (username = admin, password = prom-operator)

```bash
kubectl get svc -A | grep grafana							# check grafana services
kubectl get pods -n observability							# check pods in 'observability' namespace 

# get information about the service named "kube-prom-stack-grafana" from the namespace named "observability".
kubectl get svc kube-prom-stack-grafana -n observability -o yaml > ~/Config/grafana-svc-output.yaml		
# run/update grafana-svc-output.yaml
kubectl apply -f grafana-svc-output.yaml
```

![2-1-2-kubectl 查看状态 获得输出配置文件grafana-svc-output的yaml](/Users/boyan/Desktop/images/2-1-2-kubectl 查看状态 获得输出配置文件grafana-svc-output的yaml.png)

#### KR2 · Edit the Grafana service to allow access from the host.

Edit grafana-svc-output.yaml: 

1. add:  nodePort: 30001
2. modify:  type: NodePort 

```bash
# modify grafana-svc-output.yaml
apiVersion: v1
kind: Service
metadata:
  annotations:
    kubectl.kubernetes.io/last-applied-configuration: |
      {"apiVersion":"v1","kind":"Service","metadata":{"annotations":{"meta.helm.sh/release-name":"kube-prom-stack","meta.helm.sh/release-namespace":"observability"},"creationTimestamp":"2023-12-02T19:50:40Z","labels":{"app.kubernetes.io/instance":"kube-prom-stack","app.kubernetes.io/managed-by":"Helm","app.kubernetes.io/name":"grafana","app.kubernetes.io/version":"9.3.8","helm.sh/chart":"grafana-6.51.2"},"name":"kube-prom-stack-grafana","namespace":"observability","resourceVersion":"80294","uid":"3ecbc728-906e-4394-9d54-6d7c5f88e12b"},"spec":{"clusterIP":"10.152.183.100","clusterIPs":["10.152.183.100"],"internalTrafficPolicy":"Cluster","ipFamilies":["IPv4"],"ipFamilyPolicy":"SingleStack","ports":[{"name":"http-web","nodePort":30001,"port":80,"protocol":"TCP","targetPort":3000}],"selector":{"app.kubernetes.io/instance":"kube-prom-stack","app.kubernetes.io/name":"grafana"},"sessionAffinity":"None","type":"NodePort"},"status":{"loadBalancer":{}}}
    meta.helm.sh/release-name: kube-prom-stack
    meta.helm.sh/release-namespace: observability
  creationTimestamp: "2023-12-02T19:50:40Z"
  labels:
    app.kubernetes.io/instance: kube-prom-stack
    app.kubernetes.io/managed-by: Helm
    app.kubernetes.io/name: grafana
    app.kubernetes.io/version: 9.3.8
    helm.sh/chart: grafana-6.51.2
  name: kube-prom-stack-grafana
  namespace: observability
  resourceVersion: "83430"
  uid: 3ecbc728-906e-4394-9d54-6d7c5f88e12b
spec:
  clusterIP: 10.152.183.100
  clusterIPs:
  - 10.152.183.100
  externalTrafficPolicy: Cluster
  internalTrafficPolicy: Cluster
  ipFamilies:
  - IPv4
  ipFamilyPolicy: SingleStack
  ports:
  - name: http-web
    nodePort: 30001
    port: 80
    protocol: TCP
    targetPort: 3000
    nodePort: 30001
  selector:
    app.kubernetes.io/instance: kube-prom-stack
    app.kubernetes.io/name: grafana
  sessionAffinity: None
  type: NodePort
status:
  loadBalancer: {}
```

Update grafana-svc-output.yaml, and check grafana service status again: 

```bash
kubectl apply -f grafana-svc-output.yaml		# run/update grafana-svc-output.yaml
kubectl get svc -A | grep grafana						# check grafana service status again
```

![2-2-1-update grafana svc yaml and recheck](/Users/boyan/Desktop/images/2-2-1-update grafana svc yaml and recheck.png)

#### KR3 · Log in to the Grafana dashboard.

Grafana is using 30001 port:

![2-3-1-access Grafana](/Users/boyan/Desktop/images/2-3-1-access Grafana.png)

![2-3-2-login Grafana](/Users/boyan/Desktop/images/2-3-2-login Grafana.png)

### Object 3: Load Generator 

#### KR1 · A load generator with the following specifications

 (a) Accepts two configurable values either via a config file or environment variables.
target (The address for the load generation) and frequency (Request per second)

 (b) Generate web request to the target at the specified frequency

 (c) Collect 2 types of metrics. Average response time and accumulated number of failures

 (d) Request should timeout if it takes more than 10 seconds. Counted as failures

 (e) Test results need to be printed to the console

 (f) There are no requirements in programming language 

```java
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * LoadGenerator class to simulate network requests for load testing.
 * This class is designed to generate HTTP requests to a specified target URL at a specified frequency,
 * and track the number of total requests, failures, and the total response time.
 */
public class LoadGenerator {

    private final String target;
    private final int frequency;
    private int totalRequests;
    private int totalFailures;
    private double totalResponseTime;

    /**
     * Constructor to initialize LoadGenerator with target URL and frequency.
     *
     * @param target The URL to which the load requests are sent.
     * @param frequency The number of requests per second.
     */
    public LoadGenerator(String target, int frequency) {
        this.target = target;
        this.frequency = frequency;
        this.totalRequests = 0;
        this.totalFailures = 0;
        this.totalResponseTime = 0;
    }

    /**
     * Makes a single HTTP GET request to the target URL.
     * Records the response time and updates the total number of requests and failures.
     */
    public void makeRequest() {
        long startTime = System.nanoTime();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                totalFailures++;
            }

            connection.disconnect();
        } catch (IOException e) {
            totalFailures++;
        } finally {
            long endTime = System.nanoTime();
            totalResponseTime += (endTime - startTime) / 1e9;
        }
    }

    /**
     * Runs the load generator in a continuous loop.
     * This method generates load according to the specified frequency and prints the load information.
     */
    public void run() {
        while (true) {
            long startEpoch = System.nanoTime();

            for (int i = 0; i < frequency; i++) {
                makeRequest();
                totalRequests++;
            }

            long endEpoch = System.nanoTime();
            double elapsedSeconds = (endEpoch - startEpoch) / 1e9;

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dtf.format(LocalDateTime.now());
            System.out.println("Load Time: " + formattedDate);
            System.out.println("Failures/Requests: " + totalFailures + " / " + totalRequests);
            System.out.println("Average Response = (TotalResponseTime/TotalRequest) : " + (totalResponseTime / totalRequests) + "s\n");

            try {
                TimeUnit.SECONDS.sleep(Math.max(0, 5 - (long) elapsedSeconds));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("The load generator was interrupted!");
                break;
            }
        }
    }

    /**
     * Main method to start the LoadGenerator.
     */
    public static void main(String[] args) {
        String targetAddress = "http://192.168.0.102:30000/primecheck";	 // javabenchmarkapp
        int requestFrequency = 10; 																	 // requests per second
        LoadGenerator generator = new LoadGenerator(targetAddress, requestFrequency);
        generator.run();
    }
}
```

LoadGenerator.java local running result：

![3-1-1-LoadGenertor本地运行结果](/Users/boyan/Desktop/images/3-1-1-LoadGenertor本地运行结果.png)

#### KR2 · After programming, pack the program as a standalone Docker image and push it to the local registry at port 32000. Name the image as load-generator.

##### File structure

```
 load-generator
 	├─ Dockerfile
 	└─ LoadGenerator.java
```

```bash
# Dockerfile
# Use an official OpenJDK image as a parent image (Already installed through mobaXterm)
FROM openjdk:21-jdk

# Set the working directory to /app
WORKDIR /app

# Copy the Java source code into the container at /app
COPY . /app

# Compile the Java code
RUN javac LoadGenerator.java

# Run the application when the container lauches
CMD ["java", "LoadGenerator"]
```

##### Deploy Docker image

```bash
cd load-generator										# enter load-generator folder
docker build -t load_generator .		# use this Dockerfile to build the image "load-generator"
docker run --rm -it load_generator  # run load_generator container and delete it after exist
```

The third ctl is to check the running staus:

- `--rm`:  This is an option to automatically delete the container when it exits. This ensures that the container doesn't leave anything behind after it exits. 
- `-it`: '-i' means to keep the container's standard input open, and '-t' means to allocate a pseudo-TTY for interacting with the container. This allows to have interactive command-line sessions inside the container.

![3-2-1-build load-generator docker image](/Users/boyan/Desktop/images/3-2-1-build load-generator docker image.png)

Check if the load_generator image is running inside the container, which is a one-time operation, so the container is set to be deleted after exist:

<img src="/Users/boyan/Desktop/images/3-2-2-确认image在容器内的运行状态（一次性操作）.png" alt="3-2-2-确认image在容器内的运行状态（一次性操作）" style="zoom:50%;" />

##### Push it to the local registry at port 32000 and name the image as load-generator

```bash
docker tag load-generator:latest localhost:32000/load-generator:latest     # docker tag
docker push localhost:32000/load-generator:latest												# docker push
# make sure the image completes push successfully
curl -X GET http://localhost:32000/v2/_catalog
```

​	Tag：Mark the load-generator:latest image as localhost:32000/load-generator:latest for future reference via that tag.

​	Push：Push the previously marked load-generator:latest image to localhost:32000 in the local docker repository to store the image and make it accessible.

![3-2-3-Tag Push Check成功](/Users/boyan/Desktop/images/3-2-3-Tag Push Check成功.png)

​	restart docker and check "docker images" again：

```bash
sudo systemctl restart docker		# restart docker
docker images										# check "docker images" status again
```

​	Pushed and pulled successfully.

![3-2-4-重启docker，检查images状态](/Users/boyan/Desktop/images/3-2-4-重启docker，检查images状态.png)

### Object 4: Monitor benchmarking results 

Hints : In the Grafana panel, can specify metric: container_cpu_usage_seconds_total/ container_memory_usage_bytes 

#### KR1 · Deploy load-generator service created before

Create and edit "loadgenerator.yaml" file in ~/Config. 

```bash
touch loadgenerator.yaml		# create loadgenerator.yaml
nano loadgenerator.yaml			# modify loadgenerator.yaml
```

```bash
# loadgenerator.yaml
 apiVersion: apps/v1
 kind: Deployment
 metadata:
   name: load-generator-deployment
 spec:
   selector:
     matchLabels:
       app: load-generator
   template:
     metadata:
       labels:
         app: load-generator
     spec:
       containers:
       - name: load-generator
         image: localhost:32000/load_generator
         ports:
         - containerPort: 8081    
```

```bash
kubectl apply -f loadgenerator.yaml		# run/update loadgenerator.yaml
```

Deploy load-generator service successfully.

#### KR2+3 · During the benchmarking, create a new dashboard on Grafana and add 2 new panels which should contain queries of CPU/memory usage of the web application+Screenshot the two panels）

##### · panel1: container_cpu_usage

![3-23-1-container_cpu_usage(5m)截图](/Users/boyan/Desktop/images/3-23-1-container_cpu_usage(5m)截图.png)

```sql
# Prometheus query
sum(rate(container_cpu_usage_seconds_total{pod=~"javabenc.*"}[5m])) by (pod)
```

This is a Prometheus query that calculates the sum of CPU usage for a specific condition and groups the results by the label of the container (in this case, the pod label). Here‘s the explaination of each parts of this query:

- sum(): This is an aggregate function that sums the results of expressions in parentheses.


- rate(): Calculates the rate at which the CPU is used


- container_cpu_usage_seconds_total{pod=~"javabenc.*"}:  This is a metric selector that selects time series data with the container_cpu_usage_seconds_total metric name and a pod label, where the value of the pod label matches the regular expression javabenc.*. This will select the time series data for all containers whose pod label starts with "javabenc".


- [5m]: Specifies the time range to consider. Here, it selected the data points of the last 5 minutes to calculate the rate of CPU usage.

- by (pod): This is a grouping operation that groups the results by different values of the pod label in order to display the sum of CPU usage for each different pod in the result.


​	Putting it all together, the purpose of this query is to calculate the sum of CPU usage for all containers that match the criteria (pod labels start with "javabenc") over the past 5 minutes, and to get the sum of CPU usage for each pod, grouped by each distinct pod value.

###### ：Choose to use rate() vs irate() , in the Prometheus query：

```bash
sum(irate(container_cpu_usage_seconds_total{pod=~"javabenc.*"}[5m])) by (pod)
```

- 
  rate() : This computes the average rate over a given time horizon and is more suitable for smooth long-term trend analysis because it considers more data points over a longer period of time, thus reducing the impact of transient jitter.

- irate() is good for observing rates of change over short periods of time, such as sudden spikes or drops.


​	Here we looking at cpu usage over a certain period of time(5m), so I chose to use rate() here.	

##### · panel2: container_memory_usage

![3-23-2-container-memory-usage(5m)截图](/Users/boyan/Desktop/images/3-23-2-container-memory-usage(5m)截图.png)

```sql
# Prometheus query
max(container_memory_usage_bytes{pod=~"javabench.*"}) by (pod)
```

This Prometheus query is used to find the maximum memory usage for each container in a set of containers that meet certain criteria. Here‘s the explaination of each parts of this query:

- max() : This is an aggregation function that finds the maximum value in the result of the expression in parentheses.


- container_memory_usage_bytes{pod=~"javabench.*"}: This is a metric selector that selects time series data that meets certain criteria. Specifically, it selects time series data with the name of the container_memory_usage_bytes metric, where the value of the pod label matches the regular expression javabench.*. This will select all time series data whose pod label starts with "javabench", which typically indicates the memory usage of a container for a Java application.


- by (pod): This is a grouping operation that groups the results by different values of the pod label in order to show the maximum memory usage for each different pod in the result.


​	The goal of this query is to find the maximum memory usage for each container in a set of containers that satisfy a condition, grouped by each distinct pod value, to obtain the maximum memory usage for the containers in each pod. This is useful for monitoring and analyzing the memory usage of a Java application container. The result is a time series dataset with the maximum memory usage for each pod.

##### · *panel3: cpu_total_seconds

​	This panel monitors the cpu usage time (unit: second) ，which must be an increasing number. In panel1 on the basis of cpu_total_seconds, calculates the cpu utilization. And I just put screenshots of "cpu_total_seconds" here.

![3-23-3-cpu-total-usage(5m)截图](/Users/boyan/Desktop/images/3-23-3-cpu-total-usage(5m)截图.png)

# 2. Screenshots of running services in Kubernetes

Display a list of all services across all namespaces:

![二-1-k8s svc -a截图1](/Users/boyan/Desktop/images/二-1-k8s svc -a截图1.png)

![二-2-k8s svc -a 截图2](/Users/boyan/Desktop/images/二-2-k8s svc -a 截图2.png)

Here are some key Kubernetes services in the list (in the above figure):

- kubernetes (default namespace)：A default service that provides an entry point for API access.

- registry (Container-registry namespace)：Possibly a private Docker registry service for storing and managing the container images.
- Kubernetes-dashboard (Kubernetes-Dashboard namespace)：Kubernetes' official web user interface.
- javabenchmarkapp-service (default namespace)：the Java application service that uses NodePort to serve.
- kube-prom-stack-kube-prome-prometheus (observability namespace)：Prometheus monitoring tool that collects and stores metric data for the cluster.
- kube-prom-stack-grafana (observability namespace)：Grafana service for visualizing monitoring data.

# 3. Plots of Benchmarking results

![三-1-overall截图](/Users/boyan/Desktop/images/三-1-overall截图.png)

- **Deployment Part**: Docker is used to build the container of the application. Deploy the containers to the cluster via Kubernetes. Kubernetes takes care of managing the container lifecycle, load balancing, auto-scaling, and more.

- **Monitoring Part**: Prometheus integrates with the Kubernetes cluster to collect performance metrics about containers and the entire cluster. Prometheus periodically fetches metric data from configured targets.

- **Data Visualization Part**: Grafana connects to Prometheus, fetches data from it, and visualizes this data.

<img src="/Users/boyan/Desktop/images/三-2-Pods 20 Replicates.png" alt="三-2-Pods 20 Replicates" style="zoom:50%;" />

​	To test the Javabenchmarkapp load, the number of pods replicates in load-gnerator was adjusted from 1 to 20 at around 02:05. Here is the trend of the data compared with the analysis graph:

1. **CPU Utilization (top left panel) ** : At around 02:05, there is a significant increase in CPU utilization. This indicates that increasing the number of copies of the load-generator pods increases the demand for CPU resources because more computing power is required to process more requests. Since is the CPU utilization of the container, we can see that it fluctuates between 0.6 and 1, with 1 representing 100% CPU usage. After increasing the load, the CPU utilization quickly approaches 1, which means that the container's computing resources are fully utilized.
2. **Memory Usage (top right panel) ** : Memory usage also shows a sharp rise around 02:05. This indicates that processing more concurrent requests leads to more memory consumption. The memory usage remained relatively stable until the load increased, but then increased rapidly, possibly indicating that the Java application increased its memory footprint as it processed more load.
3. **Total CPU Usage (bottom panel) : ** Total CPU usage is a metric that always cumulatively increases, increasing at a steady rate over time.

**Conclusion** :

- Increasing the number of copies of load-Generator pods significantly increases the resource usage of the Java application, especially CPU utilization and memory usage, which reflects the direct impact of the increased load.

- An increase in CPU utilization may indicate that the container's resource limit is being approached, which may result in a decrease in processing speed or an increase in request latency.
-Sharp increases in memory usage require attention, as this can lead to memory overflow errors, especially in memory-limited environments.
- If this resource usage trend continues, you may need to consider scaling resources, optimizing application performance, or placing further limits on concurrent requests to the load-generator.

# 4. Discussion of the results and related conclusions

The coursework tasks undertaken involved a series of steps in deploying, accessing, and monitoring applications within a Kubernetes environment, using various tools and services. Here are some brief conclusion of each stage.

1. **Check connection and environment Settings **: The initial steps to make sure the environment is ready include checking the mobaXterm connection and Kubernetes cluster connection using 'kubectl'.
2. **Deployment of Kubernetes dashboard and Javabenchmarkapp Web application **: The deployment of Kubernetes dashboard and Java Web application was successfully performed using command line commands. The Kubernetes dashboard provides a user-friendly interface for managing a Kubernetes cluster.
3. **Monitoring Stack deployment **: Activation of Observability services in microk8s, enabling visualization of Kubernetes clusters with Prometheus, metrics server, and Grafana.
4. **Web Application Accessibility and service deployment **: The deployment of NodePort services makes Java Web applications accessible from the outside. This step demonstrates how the Kubernetes service exposes the application to external traffic.
5. **Load Generator Implementation: ** Developed in Java and containerized with Docker, the Load Generator plays a vital role in simulating web requests to Java applications. This tool is essential for testing the responsiveness and resilience of an application under load.
6. **Monitoring **:Grafana's integration with Prometheus provides a comprehensive view of application performance, including CPU and memory usage. Creating specific panels in Grafana allows real-time monitoring and analysis of web applications under load. In the "3. Plots of Benchmarking results" section of this document, in order to control the load, the load is increased by increasing the number of pods replicates of the load-generator, and the CPU utilization and memory usage are monitored in Grafana in real time. The changes are obvious in the figure.

​	**Overall observation: ** Docker for containerization, Kubernetes for orchestration, Prometheus for metric collection, and Grafana for visualization, the combination of these three creates a robust environment for deploying, monitoring, and analyzing web applications. The successful execution of these tasks demonstrates the effectiveness of these tools in managing and monitoring containerized applications in Kubernetes environments.