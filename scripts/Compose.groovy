importSketch "Sketch1.groovy"
importSketch "Sketch2.groovy"

sketch "s3" composedOf Sketch1 Sketch2  mergeOn "on"

export "Compose"
