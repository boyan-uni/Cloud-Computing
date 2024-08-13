# Cloud Computing 基于云计算的 Kubernetes 集群应用部署与性能监控系统
- README.md：中英版项目介绍
- Link to [CSC8110-Cloud-Computing-Report-Boyan Li-230109586.pdf](https://github.com/boyan-uni/Cloud-Computing/blob/main/CSC8110-Cloud-Computing-Report-Boyan%20Li-230109586.pdf)

## Introduction in English

### Project Description

- **Project Name**: **Cloud-Based Kubernetes Cluster Application Deployment and Performance Monitoring System**

- **Project Overview**: This project aimed to build and deploy a web application system based on a Kubernetes cluster, with load generation and performance monitoring implemented through a microservices architecture. The project included the deployment of a Kubernetes dashboard and a Java web application, configuration of the Kubernetes monitoring stack, development and containerization of a load generator, and real-time performance monitoring and analysis using Grafana.

### Technology Stack

- The project utilized Kubernetes for container orchestration and management, with Docker used for application containerization. Prometheus was employed to collect performance metrics, and Grafana was used for data visualization and monitoring. The load generator was developed in Java and deployed to the Kubernetes cluster after being packaged in a Docker container.

### Achievements

- **Application Deployment and Service Configuration**:

  - Successfully deployed the Kubernetes dashboard and containerized the Java web application using command-line tools. Created a NodePort service to ensure the web application could be accessed externally and configured the monitoring stack to enable comprehensive application monitoring.
  - Successfully configured the Kubernetes cluster monitoring, enabling the Grafana service and collecting performance metrics such as CPU and memory usage through Prometheus.

- **Load Generator Development and Optimization**:

  - Developed a load generator in Java to simulate high-frequency access requests to the web application. The load generator was packaged as a Docker image and successfully pushed to the local Docker repository.
  - After deployment in the Kubernetes cluster, the load generator's impact on the web application's performance was monitored in real-time using Grafana dashboards.

- **Performance Monitoring and Results Analysis**:

  - Monitored the CPU and memory usage of the load generator and the web application in real-time using Prometheus and Grafana. When the number of load generator replicas increased from 1 to 20, CPU utilization quickly approached 100%, and memory usage also significantly increased, reflecting the resource demands under high load conditions.
  - By continuously monitoring CPU utilization and memory usage, potential resource bottlenecks were identified, leading to optimization suggestions such as increasing resource allocation or optimizing application performance.

- **Quantitative Results**:

  - **Application Response Time**: Under high load conditions, the web application's average response time was kept within 2 seconds, demonstrating the system's strong responsiveness.
  - **Resource Utilization**: Under maximum load from the load generator, CPU utilization reached over 95%, and memory usage exceeded 80%.
  - **Load Generation Capacity**: The load generator was capable of generating over 10 concurrent requests per second, ensuring the effectiveness of the load testing.


## 中文简介

### 项目描述

- **项目名称**: **基于云计算的 Kubernetes 集群应用部署与性能监控系统**

- **项目概述**: 该项目旨在构建和部署一个基于 Kubernetes 集群的 Web 应用系统，并通过微服务架构实现负载生成和性能监控。项目包括 Kubernetes 仪表板和 Java Web 应用的部署、Kubernetes 监控栈的配置、负载生成器的开发与容器化部署、以及通过 Grafana 进行实时性能监控和分析。

### 技术栈

- 项目使用了 Kubernetes 进行容器编排与管理，并结合了 Docker 进行应用容器化部署。Prometheus 用于收集性能指标，Grafana 用于数据可视化与监控。负载生成器使用 Java 开发，并在 Docker 中封装后部署到 Kubernetes 集群中。

### 成果描述

- **应用部署与服务配置**:

  - 成功部署了 Kubernetes 仪表板，并通过命令行工具对 Java Web 应用进行容器化部署。创建了 NodePort 服务，确保 Web 应用可以通过外部访问，并配置了监控栈以实现对应用的全面监控。
  - 成功实现了 Kubernetes 集群的监控配置，启用了 Grafana 服务并通过 Prometheus 采集 CPU 和内存使用情况等性能指标。

- **负载生成器开发与优化**:

  - 使用 Java 开发了一个负载生成器，用于模拟对 Web 应用的高频率访问请求。负载生成器被打包为 Docker 镜像并成功推送到本地 Docker 仓库。
  - 负载生成器在 Kubernetes 集群中部署后，通过 Grafana 面板实时监控其生成的负载对 Web 应用性能的影响。

- **性能监控与结果分析**:

  - 通过 Prometheus 和 Grafana 对负载生成器和 Web 应用的 CPU 和内存使用情况进行了实时监控。在负载生成器副本从 1 增加到 20 时，CPU 利用率迅速接近 100%，内存使用量也显著上升，反映了应用在高负载下的资源需求。
  - 通过对 CPU 利用率和内存使用量的持续监控，识别出潜在的资源瓶颈，并提出了优化方案，如增加资源分配或优化应用性能。

- **量化成果**:

  - **应用响应时间**: 在高负载条件下，Web 应用的平均响应时间控制在 2秒 以内，表明系统具有较好的响应能力。
  - **资源利用率**: 在负载生成器的最大压力下，CPU 利用率达到了 95% 以上，内存使用率达到了 80% 以上。
  - **负载生成能力**: 负载生成器每秒能够发出 10 个以上的并发请求，确保了负载测试的有效性。
