# ImageVeil lib
This is the library behind image-veil.net.

## Build
`mvn clean install`

## Usage
### Command line
`java -jar image-veil-lib-X.Y.Z.jar PATH/TO/IMAGE.jpg PATH/TO/OUTPUT.jpg`

### Kotlin
See example usage in `src/main/kotlin/imageveil/lib/ImageVeil.kt`

`ImageVeil` can take a `File` or `InputStream`.

## Development
You can create your own Transformers by extending `Transformer` and later adding them to the queue with `imageVeilInstance.addTransformerToQueue()`