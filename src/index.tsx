import type { Double } from 'react-native/Libraries/Types/CodegenTypes';
import ChunkUploadFiles from './NativeChunkUploadFiles';
import { NativeModules, NativeEventEmitter } from 'react-native';

// ✅ Internally fetch native module from RN
const NativeChunkUploadFiles = NativeModules.ChunkUploadFiles;

// ✅ Event emitter for native events
const emitter = new NativeEventEmitter(NativeChunkUploadFiles);

// ✅ Helper functions for listener
export function addListener(
  eventType: string,
  callback: (progress: Double) => void
) {
  return emitter.addListener(eventType, callback);
}

// ✅ Export native methods (post, etc.)
export default NativeChunkUploadFiles;

export function post(
  to: string,
  key: string,
  uri: string,
  token?: string,
  otherParams?: Object
): Promise<Object> {
  return ChunkUploadFiles.post(to, key, uri, token, otherParams);
}
