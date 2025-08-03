import Foundation
import Alamofire
import AVFoundation
import React

@objc(ChunkUploadFiles)
class ChunkUploadFiles: RCTEventEmitter{
  
  enum UploadError: Error {
    case emptyOrInvalidURL
    case emptyOrInvalidKey
    case emptyOrInvalidFileURI
    case emptyOrInvalidFileName
    case emptyOrInvalidType
//        case invalidTypeURL
      
    var localizedDescription: String {
      switch self {
        case .emptyOrInvalidURL:
          return "No URL provided for upload or URL is not a string"
        case .emptyOrInvalidKey:
          return "No key provided for upload or key is not a string"
        case .emptyOrInvalidFileURI:
          return "No file URI provided for upload or URI is not a string"
        case .emptyOrInvalidFileName:
          return "No file name provided for upload or file name is not a string"
        case .emptyOrInvalidType:
          return "No file type provided for upload or file type is not a string"
      }
    }
  }

  @objc
  func post(_ to: String, key: String, fileURI: String, token: String? = nil, otherParams: [String: Any]?, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        
    do{
      guard !to.isEmpty else {
        throw UploadError.emptyOrInvalidURL
      }
      guard !key.isEmpty else {
        throw UploadError.emptyOrInvalidKey
      }
      guard !fileURI.isEmpty else {
        throw UploadError.emptyOrInvalidFileURI
      }
            
      let fileURL = URL(string: fileURI)
            
      var headers: HTTPHeaders = [
        "Content-type": "multipart/form-data"
      ]
      if let value = token, !value.isEmpty {
        headers["Authorization"] = "Bearer \(value)"
      }
    
      AF.upload(multipartFormData: { multipartFormData in
          multipartFormData.append(fileURL!, withName:key)
          if let jsonObject = otherParams {
            self.appendJSONObjectToMultipart(jsonObject: jsonObject, to: multipartFormData)
          }
        }, to: to, method: .post, headers:headers)
        .uploadProgress { progress in
          self.sendEvent(withName:"progress", body:progress.fractionCompleted)
        }
        .responseData { response in
          switch response.result {
            case .success(let data):
              let json = try? JSONSerialization.jsonObject(with: data, options: [])
              resolve(json);
            case .failure(let error):
              let statusCode = response.response?.statusCode
              reject(String(statusCode ?? -1),error.localizedDescription, error)
            }
        }
    }catch let error as UploadError {
      reject("-1", error.localizedDescription, error)
    }catch {
      reject("-1", error.localizedDescription, error)
    }
  }
  
  func appendJSONObjectToMultipart(jsonObject: [String: Any], to multipartFormData: MultipartFormData) {
      for (key, value) in jsonObject {
          switch value {
              
          case let stringValue as String:
              multipartFormData.append(Data(stringValue.utf8), withName: key)
              
          case let intValue as Int:
              multipartFormData.append(Data("\(intValue)".utf8), withName: key)
              
          case let doubleValue as Double:
              multipartFormData.append(Data("\(doubleValue)".utf8), withName: key)
              
          case let floatValue as Float:
              multipartFormData.append(Data("\(floatValue)".utf8), withName: key)
              
          case let boolValue as Bool:
              multipartFormData.append(Data("\(boolValue)".utf8), withName: key)
              
          case let dictValue as [String: Any]:
              if let jsonData = try? JSONSerialization.data(withJSONObject: dictValue, options: []) {
                  multipartFormData.append(jsonData, withName: key)
              }
              
          case let arrayValue as [Any]:
              if let jsonData = try? JSONSerialization.data(withJSONObject: arrayValue, options: []) {
                  multipartFormData.append(jsonData, withName: key)
              }
              
          case _ as NSNull:
              // Optional: skip or send empty string
              multipartFormData.append(Data("".utf8), withName: key)
              
          default:
              // Fallback: Convert to string
              multipartFormData.append(Data("\(value)".utf8), withName: key)
          }
      }
  }

  override func supportedEvents() -> [String]! {
      return ["progress"]
  }

  @objc
  override static func requiresMainQueueSetup() -> Bool {
    return false
  }
}
