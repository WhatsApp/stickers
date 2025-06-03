//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

struct StickerEmojis {

    static func canonicalizedEmojis(rawEmojis: [String]?) throws -> [String]? {

        guard let rawEmojis = rawEmojis else { return nil }

        if rawEmojis.count > Limits.MaxEmojisCount {
          throw StickerPackError.tooManyEmojis
        }

        var canonicalizedEmojis: [String] = []

        rawEmojis.forEach { rawEmoji in

          var emojiToAdd = canonicalizedEmoji(emoji: rawEmoji)

          // If the emoji somehow isn't canonicalized, we'll use the original emoji
          if emojiToAdd.isEmpty {
            emojiToAdd = rawEmoji
          }

          canonicalizedEmojis.append(emojiToAdd)
        }

        return canonicalizedEmojis
    }

    private static func canonicalizedEmoji(emoji: String) -> String {
        var nonExtensionUnicodes: [Character] = []

        for scalar in emoji.unicodeScalars {
            switch scalar.value {
            case 0x1F600...0x1F64F,    // Emoticons
            0x1F300...0x1F5FF,         // Misc symbols and pictographs
            0x1F680...0x1F6FF,         // Transport and maps
            0x2600...0x26FF,           // Misc symbols
            0x2700...0x27BF,           // Dingbats
            0x1F1E6...0x1F1FF,         // Flags
            0x1F900...0x1F9FF,         // Supplemental symbols and pictographs
            0x200D:                    // Zero-width joiner
                nonExtensionUnicodes.append(Character(UnicodeScalar(scalar.value)!))

            default:
                continue
            }
        }

        var canonicalizedEmoji = ""

        nonExtensionUnicodes.forEach { canonicalizedEmoji.append($0) }
        return canonicalizedEmoji
    }
}

struct StickerAccessibilityText {

    static func accessibilityTextIfCompliant(
        accessibilityText: String?,
        animated: Bool
    ) throws -> String? {
        guard let accessibilityText else { return nil }
        
        if animated {
            guard accessibilityText.count <= Limits.MaxAnimatedStickerAccessibilityTextCharLimit else {
                throw StickerPackError.animatedStickerAccessibilityTextTooLong
            }
        } else {
            guard accessibilityText.count <= Limits.MaxStaticStickerAccessibilityTextCharLimit else {
                throw StickerPackError.staticStickerAccessibilityTextTooLong
            }
        }
        
        return accessibilityText
    }
}

/**
 *  Main class that deals with each individual sticker.
 */
class Sticker {

    let imageData: ImageData
    let emojis: [String]?
    let accessibilityText: String?

    var bytesSize: Int64 {
        return imageData.bytesSize
    }

    /**
     *  Initializes a sticker with an image file and emojis.
     *
     *  - Parameter filename: name of the image in the bundle, including extension. Must be either png or webp.
     *  - Parameter emojis: emojis associated with this sticker.
     *  - Parameter accessibilityText: accessibility text associated with this sticker.
     *
     *  - Throws:
     - .fileNotFound if file has not been found
     - .unsupportedImageFormat if image is not png or webp
     - .imageTooBig if the image file size is above the supported limit (100KB for static, 500KB for animated)
     - .invalidImage if the image file size is 0KB
     - .incorrectImageSize if the image is not within the allowed size
     - .tooManyEmojis if there are too many emojis assigned to the sticker
     - .minFrameDurationTooShort if the minimum frame duration is too short (less than 8ms)
     - .totalAnimationDurationTooLong if the total animation duration is too long (more than 10s)
     */
    init(
        contentsOfFile filename: String,
        emojis: [String]?,
        accessibilityText: String?
    ) throws {
        self.imageData = try ImageData.imageDataIfCompliant(contentsOfFile: filename, isTray: false)
        self.emojis = try StickerEmojis.canonicalizedEmojis(rawEmojis: emojis)
        self.accessibilityText = try StickerAccessibilityText.accessibilityTextIfCompliant(
            accessibilityText: accessibilityText,
            animated: imageData.animated
        )
    }

    /**
     *  Initializes a sticker with image data, type and emojis.
     *
     *  - Parameter imageData: Data of the image. Must be png or webp encoded data
     *  - Parameter type: format type of the sticker (png or webp)
     *  - Parameter emojis: array of emojis associated with this sticker.
     *  - Parameter accessibilityText: accessibility text associated with this sticker.
     *
     *  - Throws:
     - .imageTooBig if the image file size is above the supported limit (100KB for static, 500KB for animated)
     - .invalidImage if the image file size is 0KB
     - .incorrectImageSize if the image is not within the allowed size
     - .tooManyEmojis if there are too many emojis assigned to the sticker
     - .minFrameDurationTooShort if the minimum frame duration is too short (less than 8ms)
     - .totalAnimationDurationTooLong if the total animation duration is too long (more than 10s)
     */
    init(
        imageData: Data,
        type: ImageDataExtension,
        emojis: [String]?,
        accessibilityText: String?
    ) throws {
        self.imageData = try ImageData.imageDataIfCompliant(rawData:imageData, extensionType: type, isTray: false)
        self.emojis = try StickerEmojis.canonicalizedEmojis(rawEmojis: emojis)
        self.accessibilityText = try StickerAccessibilityText.accessibilityTextIfCompliant(
            accessibilityText: accessibilityText,
            animated: self.imageData.animated
        )
    }

    func copyToPasteboardAsImage() {
        if let image = imageData.image {
            Interoperability.copyImageToPasteboard(image: image)
        }
    }
}
