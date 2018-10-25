//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

/**
 *  Represents a variety of errors related to stickers.
 */
enum StickerPackError: Error {
    case fileNotFound
    case emptyString
    case unsupportedImageFormat(String)
    case imageTooBig(Int64)
    case incorrectImageSize(CGSize)
    case animatedImagesNotSupported
    case stickersNumOutsideAllowableRange
    case stringTooLong
    case tooManyEmojis
}

/**
 *  Main class that handles sticker packs, a set of stickers.
 */
class StickerPack {

    let identifier: String
    let name: String
    let publisher: String
    let trayImage: ImageData
    let publisherWebsite: String?
    let privacyPolicyWebsite: String?
    let licenseAgreementWebsite: String?

    var stickers: [Sticker]

    var bytesSize: Int64 {
        var totalBytes: Int64 = Int64(name.utf8.count + publisher.utf8.count + trayImage.data.count)
        for sticker in stickers {
            totalBytes += sticker.bytesSize
        }
        return totalBytes
    }

    var formattedSize: String {
        return ByteCountFormatter.string(fromByteCount: bytesSize, countStyle: .file)
    }

    /**
     *  Initializes a sticker pack with a name, publisher and tray image name.
     *
     *  - Parameter name: title of the sticker pack
     *  - Parameter publisher: publisher of the sticker pack
     *  - Parameter publisherWebsite: website of publisher
     *  - Parameter privacyPolicyWebsite: website of privacy policy
     *  - Parameter licenseAgreementWebsite: website of license agreement
     *
     *  - Throws:
     - .emptyString if the name and publisher are empty strings
     - .stringTooLong if the name and publisher are more than 128 character
     - .fileNotFound if tray image file has not been found
     - .unsupportedImageFormat if tray image file is not png or webp
     - .imageTooBig if the image file size is above the supported limit (100KB)
     - .incorrectImageSize if the tray image is not within the allowed size
     - .animatedImagesNotSupported if the tray image is animated
     */
    init(identifier: String, name: String, publisher: String, trayImageFileName: String, publisherWebsite: String?, privacyPolicyWebsite: String?, licenseAgreementWebsite: String?) throws {
        guard !name.isEmpty && !publisher.isEmpty && !identifier.isEmpty else {
            throw StickerPackError.emptyString
        }

        guard name.count <= Limits.MaxCharLimit128 && publisher.count <= Limits.MaxCharLimit128 && identifier.count <= Limits.MaxCharLimit128 else {
            throw StickerPackError.stringTooLong
        }

        self.identifier = identifier
        self.name = name
        self.publisher = publisher

        let trayCompliantImageData: ImageData = try ImageData.imageDataIfCompliant(contentsOfFile: trayImageFileName, isTray: true)
        self.trayImage = trayCompliantImageData

        stickers = []

        self.publisherWebsite = publisherWebsite
        self.privacyPolicyWebsite = privacyPolicyWebsite
        self.licenseAgreementWebsite = licenseAgreementWebsite
    }

    /**
     *  Initializes a sticker pack with a name, publisher and tray image data.
     *
     *  - Paramter identifier: identifier of the sticker pack
     *  - Parameter name: title of the sticker pack
     *  - Parameter publisher: publisher of the sticker pack
     *  - Parameter trayImagePNGData: the PNG data of the tray image
     *  - Parameter publisherWebsite: website of publisher
     *  - Parameter privacyPolicyWebsite: website of privacy policy
     *  - Parameter licenseAgreementWebsite: website of license agreement
     *
     *  - Throws:
     - .emptyString if any string parameter is empty
     - .stringTooLong if any string is too long
     - .imageTooBig if the tray image file size is above the supported limit (100KB)
     - .incorrectImageSize if the tray image is not within the allowed size
     - .animatedImagesNotSupported if the tray image is animated
     */
    init(identifier: String, name: String, publisher: String, trayImagePNGData: Data, publisherWebsite: String?, privacyPolicyWebsite: String?, licenseAgreementWebsite: String?) throws {
        guard !name.isEmpty && !publisher.isEmpty && !identifier.isEmpty else {
            throw StickerPackError.emptyString
        }

        guard name.count <= Limits.MaxCharLimit128 && publisher.count <= Limits.MaxCharLimit128 && identifier.count <= Limits.MaxCharLimit128 else {
            throw StickerPackError.stringTooLong
        }

        self.identifier = identifier
        self.name = name
        self.publisher = publisher

        let trayCompliantImageData: ImageData = try ImageData.imageDataIfCompliant(rawData: trayImagePNGData, extensionType: .png, isTray: true)
        self.trayImage = trayCompliantImageData

        stickers = []

        self.publisherWebsite = publisherWebsite
        self.privacyPolicyWebsite = privacyPolicyWebsite
        self.licenseAgreementWebsite = licenseAgreementWebsite
    }

    /**
     *  Adds a sticker to the current sticker pack.
     *
     *  - Parameter filename: file name of the sticker (png or webp).
     *  - Parameter emojis: emojis associated with the sticker.
     *
     *  - Throws:
     - .stickersNumOutsideAllowableRange if current number of stickers is not withing limits
     - All exceptions from Sticker(contentsOfFile:emojis:)
     */
    func addSticker(contentsOfFile filename: String, emojis: [String]?) throws {
        guard stickers.count <= Limits.MaxStickersPerPack else {
            throw StickerPackError.stickersNumOutsideAllowableRange
        }

        let sticker: Sticker = try Sticker(contentsOfFile: filename, emojis: emojis)

        stickers.append(sticker)
    }

    /**
     *  Adds a sticker to the current sticker pack.
     *
     *  - Parameter imageData: image data of the sticker
     *  - Parameter type: extension type of the data (png or webp)
     *  - Parameter emojis: emojis associated with the sticker.
     *
     *  - Throws:
     - .stickersNumOutsideAllowableRange if current number of stickers is not withing limits
     - All exceptions from Sticker(imageData:type:emojis:)
     */
    func addSticker(imageData: Data, type: ImageDataExtension, emojis: [String]?) throws {
        guard stickers.count <= Limits.MaxStickersPerPack else {
            throw StickerPackError.stickersNumOutsideAllowableRange
        }

        let sticker: Sticker = try Sticker(imageData: imageData, type: type, emojis: emojis)

        stickers.append(sticker)
    }

    /**
     *  Sends current sticker pack to WhatsApp.
     *
     *  - Parameter completionHandler: block that gets called when the sticker pack has been wrapped
     *    into a format that WhatsApp can read and WhatsApp is about to open. Called on the main
     *    queue.
     */
    func sendToWhatsApp(completionHandler: @escaping (Bool) -> Void) {
        StickerPackManager.queue.async {
            var json: [String: Any] = [:]
            json["identifier"] = self.identifier
            json["name"] = self.name
            json["publisher"] = self.publisher
            json["tray_image"] = UIImagePNGRepresentation(self.trayImage.image!)?.base64EncodedString()

            var stickersArray: [[String: Any]] = []
            for sticker in self.stickers {
                var stickerDict: [String: Any] = [:]

                if let imageData = sticker.imageData.webpData {
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
