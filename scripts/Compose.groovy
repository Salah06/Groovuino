importSketch "./scripts/Sketch1.groovy"
importSketch "./scripts/Sketch2.groovy"

sketch "s3" isComposedBy(Sketch1, Sketch2) withStrategy manual mergingState("on", "on") and manual mergingState("off", "off")

export "Compose"
