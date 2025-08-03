# react-native-chunk-upload-files

this library sends the files in small chunks.

## Installation

```sh
npm install react-native-chunk-upload-files
```

## Usage


```js
import { post } from 'react-native-chunk-upload-files';

post(uploadUrl, key, uri, "xyz", {key1: "true", key2: 1, key3:{value : "xyz"}}).then(res => {
    console.log("res", res);
    // {message: "Successfully Uploaded !!", status: true}
}).catch(err => console.log("error", err))

```

post(options: UploadFileOptions): Promise<Object>
type UploadFileOptions = {
    to: string;          // URL to upload file to
    key:string;          // the key in which the file goes to the backend server
    uri:string;          // URI of the file
    token?: string;      // Optional, for example Authorization token/ JWT token
    otherParams?: Object // Optional, otherParams is a json object which can have any key/value pair such as key1: "true", key2: 1, key3: {value : "xyz"} which you want to pass in request along with the file.
}

| Property      | Type                | Description                                                              |
| ------------- | ------------------- | ------------------------------------------------------------------------ |
| `to`          | `string`            | **Required**. Upload URL                                                 |
| `key`         | `string`            | **Required**. The field name for the file in the multipart form          |
| `uri`         | `string`            | **Required**. URI of the file to be uploaded                             |
| `token`       | `string` (optional) | Optional authorization token (e.g., JWT)                                 |
| `otherParams` | `Object` (optional) | Additional key-value pairs (can be string, number, nested objects, etc.) |




## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
