# Distributed Text Processing System  
###  Deployment in a Cluster

## Authors
- Javier González Benítez  
- Jorge González Benítez  
- Alejandro Del Toro Acosta  
- Luis Guillén Servera  

📅 January 14, 2025

---

## Abstract

This project presents a distributed architecture for large-scale text processing using **Java**, **Hazelcast**, and **multithreaded components**. The system is designed to efficiently download, index, and query large collections of books stored in a datalake.

The architecture includes:
- A **Crawler** for downloading books
- An **Indexer** for building inverted indexes
- A **RESTful Query API** for searching and retrieving metadata

The system was tested with over **2,150 books**, demonstrating scalability, fault tolerance, and efficient query performance using distributed data storage and synchronization.

---

## Introduction

The exponential growth of digital text content has increased the need for scalable systems capable of processing large datasets efficiently. Applications such as search engines, recommendation systems, and data analysis tools require optimized solutions for ingestion, storage, indexing, and querying.

This project addresses these challenges by leveraging:
- **Multithreading** for performance
- **Hazelcast** for distributed storage and synchronization
- **Modular design** for scalability and maintainability

---

## System Architecture

The system is composed of three main modules:

### 1. Crawler
- Multithreaded book downloader
- Uses `ExecutorService`
- Stores books in a structured datalake
- Handles retries and failed downloads

### 2. Indexer
- Processes downloaded books
- Builds an **inverted index**
- Extracts metadata (title, author, language) using regular expressions
- Stores data in:
  - **JSON Datamart** (file-based)
  - **MongoDB Datamart** (distributed and scalable)

### 3. Query API
- RESTful API built with **SparkJava**
- Supports word-based and multi-word searches
- Uses **Hazelcast** as a distributed cache
- Provides low-latency responses

---

## Methodology

### Objectives
- **Efficient data ingestion**
- **Scalable indexing**
- **Low-latency querying**

### Implementation Details

- **Crawler Module**
  - Concurrent downloads using a fixed thread pool
  - Optimized for network and I/O constraints

- **Indexing Module**
  - Parallel file processing
  - Redundant storage in JSON and MongoDB

- **Query API**
  - Thread-per-request model
  - Hazelcast for fast distributed lookups

### Testing and Evaluation
- Dataset: **2,150+ books** from Project Gutenberg
- Metrics:
  - Download speed
  - Indexing time
  - Query latency
  - CPU and memory usage

---

## Experiments

### Experimental Setup
Each module was tested independently and as part of the full pipeline using the complete dataset.

### Evaluation Metrics
- Download speed (books/min)
- Indexing time
- Query latency
- Thread utilization
- Resource utilization

### Results Summary

#### Crawler
- Optimal threads: **4**
- Avg speed: **50 books/min**
- Peak speed: **75 books/min**

#### Indexer
- Optimal threads: **4**
- Total indexing time: **35 minutes**
- Balanced CPU and memory usage

#### Hazelcast Processor
- Single-threaded for consistency
- Datalake ingestion: **< 10 minutes**
- Datamart ingestion: **~15 minutes**

#### Query API
- Single-word queries: **~200 ms**
- Multi-word queries: **~400 ms**
- Stable latency under heavy load

---

## Benchmark

### Crawler Statistics

| Metric | Value |
|------|------|
| Total Time | 5 seconds |
| Books Downloaded | 61 |
| Failed Downloads | 1 |
| Warnings | 2 |
| Total Size | 3 MB |
| Format | TXT |
| Download Speed | 12.2 books/sec |

### Indexing Performance

| Run | Storage | Duration | Words Processed |
|----|--------|----------|----------------|
| 1st | JSON | Instant | 86,696 |
| 1st | MongoDB | 1:12 min | 86,696 |
| 2nd | JSON | 1:05 min | 86,696 |
| 2nd | MongoDB | 1:08 min | 86,696 |

### Query Performance

| Storage | Avg Time (ms) | CPU (%) | Memory (MB) |
|-------|--------------|--------|-------------|
| JSON | 24.3 | 15.2 | 45.3 |
| MongoDB | 31.7 | 17.8 | 55.7 |

---

## Conclusion

Key findings:
- **JSON indexing** is faster but less scalable
- **MongoDB** offers better scalability and query capabilities
- **Java** significantly outperforms Python in both indexing and querying
- Hazelcast enables fault tolerance and efficient distributed querying

The choice of storage and language should depend on performance requirements, dataset size, and query complexity.

---

## Code and Data

All source code and datasets are available at:

🔗 https://github.com/jorgegonzalezbenitez/GreenLanterns

---

## Future Work

Planned improvements include:
- Distributed processing with Apache Hadoop or Spark
- Enhanced error handling and retry mechanisms
- Performance visualization tools
- Real-time indexing updates
- Support for domain-specific datasets (e.g. scientific or legal texts)

---

## Project Structure Overview

- Introduction  
- Methodology  
- System Design  
- Implementation Details  
- Testing and Evaluation  
- Experiments  
- Benchmark  
- Conclusion  
- Code and Data  
- Future Work  
