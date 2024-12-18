rootProject.name = "sample"

include("application")
include("library")
include("aggregation")

// Plugin with custom test task
includeBuild("plugin")

// Example TAPI consumer
includeBuild("consumer")