//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

class WebPManager {

    static let shared: WebPManager = WebPManager()

    func isAnimated(webPData data: Data) -> Bool {
        guard let decoder = YYImageDecoder(data: data, scale: 1.0) else { return false }

        return decoder.frameCount > 1
    }

    func minFrameDuration(webPData data: Data) -> TimeInterval {
        guard let decoder = YYImageDecoder(data: data, scale: 1.0) else { return -1 }
        guard decoder.frameCount > 1 else { return -1 }

        var minFrameDuration = decoder.frameDuration(at: 0)
        for index in 1..<decoder.frameCount {
            let frameDuration = decoder.frameDuration(at: index)
            if frameDuration < minFrameDuration {
                minFrameDuration = frameDuration
            }
        }

        return minFrameDuration
    }

    func totalAnimationDuration(webPData data: Data) -> TimeInterval {
        guard let decoder = YYImageDecoder(data: data, scale: 1.0) else { return -1 }
        guard decoder.frameCount > 1 else { return -1 }

        var totalAnimationDuration = decoder.frameDuration(at: 0)
        for index in 1..<decoder.frameCount {
            totalAnimationDuration += decoder.frameDuration(at: index)
        }

        return totalAnimationDuration
    }

    func decode(webPData data: Data) -> [UIImage]? {
        guard let decoder = YYImageDecoder(data: data, scale: 1.0) else { return nil }

        var images: [UIImage] = []
        for index in 0..<decoder.frameCount {
            guard let frame = decoder.frame(at: index, decodeForDisplay: true) else {
                continue
            }
            guard let image = frame.image else {
                continue
            }
            images.append(image)
        }
        if images.count == 0 {
            return nil
        }
        return images
    }

    func encode(pngData data: Data) -> Data? {
        guard let encoder = YYImageEncoder(type: YYImageType.webP) else { return nil }

        encoder.addImage(with: data, duration: 0.0)
        return encoder.encode()
    }
}
