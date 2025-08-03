#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <ChunkUploadFilesSpec/ChunkUploadFilesSpec.h>

@interface RCT_EXTERN_MODULE(ChunkUploadFiles, RCTEventEmitter <NativeChunkUploadFilesSpec>)
RCT_EXTERN_METHOD(post:(NSString *)to key:(NSString *)key fileURI:(NSString *)fileURI token:(nullable NSString *)token otherParams:(nullable NSDictionary<NSString *, id> *)otherParams resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeChunkUploadFilesSpecJSI>(params);
}

@end
