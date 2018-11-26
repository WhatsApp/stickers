//
//  JSONDownLoader.swift
//  WAStickersThirdParty
//
//  Created by Poornima Sreedhar on 26/11/18.
//  Copyright © 2018 WhatsApp. All rights reserved.
//

import Foundation
enum Result<Value> {
    case success(Value)
    case failure(Error)
}
class JSONDownLoader {
    func getStickerPackes(form stringUrl:String, completion: ((Result<WAStickerPackResponse>) -> Void)?) {
        let url = URL(string: stringUrl)!
        let request = URLRequest(url: url)
        
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (responseData, response, responseError) in
            DispatchQueue.main.async {
                if let error = responseError {
                    completion?(.failure(error))
                } else if let jsonData = responseData {
                    // Now we have jsonData, Data representation of the JSON returned to us
                    // from our URLRequest...
                    
                    // Create an instance of JSONDecoder to decode the JSON data to our
                    // Codable struct
                    let decoder = JSONDecoder()
                    
                    do {
                        // We would use Post.self for JSON representing a single Post
                        // object, and [Post].self for JSON representing an array of
                        // Post objects
                        let waStickerPackResponse = try decoder.decode(WAStickerPackResponse.self, from: jsonData)
                        completion?(.success(waStickerPackResponse))
                    } catch {
                        completion?(.failure(error))
                    }
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Data was not retrieved from request"]) as Error
                    completion?(.failure(error))
                }
            }
        }
        
        task.resume()
    }
}
