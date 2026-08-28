# Database Design

StartupForge AI uses PostgreSQL for transactional application data.

## Main Domain Relationship

```text
User
 |
 +---- Project
          |
          +---- Generation
          |       |
          |       +---- Agent Results
          |
          +---- Blueprint
```

## Blueprint

The Core Service stores the consolidated startup blueprint with:

- project reference
- generation reference
- title
- executive summary
- blueprint content
- creation timestamp
- update timestamp

Indexes are provided for project, generation and creation-time lookup.

## Vector Search

The architecture includes PostgreSQL with pgvector for retrieval-augmented AI workflows. Vector persistence can support context retrieval for startup research and recommendations.
