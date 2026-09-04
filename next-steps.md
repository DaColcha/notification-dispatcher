# 🚀 Notification Dispatcher: Learning Roadmap

This project is a playground for practicing Event-Driven Architecture (EDA) using Spring Boot, Apache Kafka, and React. The goal is to explore the core concepts of asynchronous communication and distributed systems through hands-on implementation.

## 🎓 Learning Milestones

### 1. Advanced Routing & Priority Engine
*Move beyond simple channel routing to a rule-based delivery system.*

- **What to implement**: Create a `RoutingService` that determines the delivery channel based on the message payload. For example, a "HIGH" priority notification could be routed to both Email and Discord, while a "LOW" priority one only goes to Email.
- **Learning Concepts**: 
    - **Strategy Pattern**: Using different strategies for routing logic.
    - **Content-Based Router**: A fundamental EDA pattern where the message content determines the destination.
    - **Kafka Topic Management**: Managing multiple specialized topics.

### 2. The "Time Machine" (Event Storage & History)
*Introduce persistence to an asynchronous flow.*

- **What to implement**: Integrate a database (PostgreSQL or MongoDB) to archive every notification event that passes through the system. Build a "Notification History" view in the React frontend to browse past events.
- **Learning Concepts**: 
    - **Event Sourcing Basics**: The idea that the state of a system can be reconstructed from a sequence of events.
    - **Async Persistence**: Integrating a database into a Kafka consumer without blocking the event loop.
    - **Frontend Data Fetching**: Implementing pagination or infinite scroll for history logs.

### 3. Schema Evolution with Avro/Protobuf
*Explore professional data serialization in Kafka.*

- **What to implement**: Replace JSON payloads with **Apache Avro** or **Protobuf**. Set up a **Confluent Schema Registry** container in `docker-compose.yml` and update the Spring Boot producers and consumers to use binary formats.
- **Learning Concepts**: 
    - **Serialization/Deserialization (SerDes)**: Understanding how binary formats improve performance and reliability.
    - **Schema Evolution**: Practicing "Backward" and "Forward" compatibility when adding new fields to events.
    - **Payload Optimization**: Comparing payload sizes between JSON and Avro.

### 4. Visualizing the "Event Flow" (The Observer UI)
*Make the "invisible" async flow visible.*

- **What to implement**: Use a library like `reactflow` to create a real-time dashboard. As a message moves through the system (`API` $\rightarrow$ `Kafka Topic` $\rightarrow$ `Consumer` $\rightarrow$ `WebSocket`), the UI should highlight the current node the message is occupying.
- **Learning Concepts**: 
    - **Advanced React State**: Managing complex graph-based state in the UI.
    - **System Monitoring**: Creating a dedicated WebSocket channel for system metadata.
    - **Mental Mapping**: Visualizing distributed system interactions.

### 5. The "Chaos" Scaling Experiment
*Deep dive into Kafka Consumer Group internals.*

- **What to implement**: Create a "Scaling Controller" (simple API or script) to spin up/down additional instances of the consumer services. Implement a UI element to see which Kafka partition is being handled by which specific consumer instance.
- **Learning Concepts**: 
    - **Consumer Groups**: Understanding how Kafka distributes load.
    - **Rebalance Protocol**: Observing what happens when a consumer joins or leaves the group.
    - **Horizontal Scaling**: Testing system behavior under increased concurrency using `docker-compose up --scale`.

---
*This document is a living roadmap. Complete a milestone, mark it off, and dive into the next one!*
