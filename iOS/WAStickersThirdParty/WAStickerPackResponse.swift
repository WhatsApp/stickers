//
//  StickerPackResponse.swift
//  WAStickersThirdParty
//
//  Created by Poornima Sreedhar on 26/11/18.
//  Copyright © 2018 WhatsApp. All rights reserved.
//

import Foundation
let jsonurl = "https://api.myjson.com/bins/eg7i2"
// Codable to Parse the JsonData into Model

//MARK:WAStickerPackResponse
//Response Model Class
struct WAStickerPackResponse: Codable {
    let iOSAppstoreLink: String = ""
    let androidAppstoreLink: String = ""
    let stickerPacks:[WAStickerPack]?
    
    enum CodingKeys: String, CodingKey {
        case iOSAppstoreLink = "ios_app_store_link"
        case androidAppstoreLink = "android_play_store_link"
        case stickerPacks = "sticker_packs"

    }
    
}

// Sticker Pack
struct WAStickerPack: Codable {
    let identifier: String
    let name: String
    let publisher: String
    let trayImage: String
    let publisherWebsite: String?
    let privacyPolicyWebsite: String?
    let licenseAgreementWebsite: String?
    let stickers:[WASticker]
    enum CodingKeys: String, CodingKey {
        case identifier
        case name
        case publisher
        case trayImage = "tray_image_file"
        case publisherWebsite = "publisher_website"
        case privacyPolicyWebsite = "privacy_policy_website"
        case licenseAgreementWebsite = "license_agreement_website"
        case stickers
        
    }
    
    var imageData: ImageData?  {
        do {
           return try ImageData.imageDataIfCompliant(contentsOfFile: trayImage, isTray: true)
        } catch {
           return nil
        }
       
    }

    var bytesSize: Int64 {
        var imagedata = 0
        if let size = imageData?.data.count {
            imagedata = size
        }
        var totalBytes: Int64 = Int64(name.utf8.count + publisher.utf8.count + imagedata)
        for sticker in stickers {
            totalBytes += sticker.bytesSize
        }
        return totalBytes
    }
    
    var formattedSize: String {
        return ByteCountFormatter.string(fromByteCount: bytesSize, countStyle: .file)
    }
    func sendToWhatsApp(completionHandler: @escaping (Bool) -> Void) {
        StickerPackManager.queue.async {
            var json: [String: Any] = [:]
            json["identifier"] = self.identifier
            json["name"] = self.name
            json["publisher"] = self.publisher
            json["tray_image"] = UIImagePNGRepresentation((self.imageData?.image!)!)?.base64EncodedString()
            
            var stickersArray: [[String: Any]] = []
            for sticker in self.stickers {
                var stickerDict: [String: Any] = [:]
                
                if let imageData = sticker.imageData?.webpData {
                    stickerDict["image_data"] = imageData.base64EncodedString()
                } else {
                    print("Skipping bad sticker data")
                    continue
                }
                
                stickerDict["emojis"] = sticker.emojis
                
                stickersArray.append(stickerDict)
            }
            json["stickers"] = stickersArray
            
            let result = Interoperability.send(json: json)
            DispatchQueue.main.async {
                completionHandler(result)
            }
        }
    }
}

//Sticker
struct WASticker: Codable {
    let imageFile: String
    let emojis: [String]?
    enum CodingKeys: String, CodingKey {
        case imageFile = "image_file"
        case emojis
    }
    var imageData: ImageData? {
        do {
            return try ImageData.imageDataIfCompliant(contentsOfFile: imageFile, isTray: false)
        } catch {
            return nil
        }
    }
    func copyToPasteboardAsImage() {
        if let image = imageData?.image {
            Interoperability.copyImageToPasteboard(image: image)
        }
    }
    var bytesSize: Int64 {
        if let byteSize = imageData?.bytesSize {
           return byteSize
        }
        return Int64(0)
    }
}

let testStickerPackJson = """
{
    "ios_app_store_link" : "",
    "android_play_store_link" : "",
    "sticker_packs" : [
    {
    "identifier": "cuppyID",
    "name": "Cuppy",
    "publisher": "Jane Doe",
    "tray_image_file" : "tray_Cuppy.png",
    "publisher_website" : "",
    "privacy_policy_website" : "",
    "license_agreement_website" : "",
    "stickers": [
    {
    "image_file": "01_Cuppy_smile.png",
    "emojis": ["☕","🙂"]
    }
  ]
 }]
}
"""
