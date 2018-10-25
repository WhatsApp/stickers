//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

struct Limits {
    static let MaxStickerFileSize: Int = 100 * 1024
    static let MaxTrayImageFileSize: Int = 50 * 1024

    static let TrayImageDimensions: CGSize = CGSize(width: 96, height: 96)
    static let ImageDimensions: CGSize = CGSize(width: 512, height: 512)

    static let MinStickersPerPack: Int = 3
    static let MaxStickersPerPack: Int = 30

    static let MaxCharLimit128: Int = 128

    static let MaxEmojisCount: Int = 3
}
