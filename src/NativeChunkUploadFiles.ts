import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  post(
    to: string,
    key: string,
    uri: string,
    token?: string,
    otherParams?: Object
  ): Promise<Object>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ChunkUploadFiles');
