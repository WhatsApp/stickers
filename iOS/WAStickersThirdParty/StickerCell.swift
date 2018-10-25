//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

final class StickerCell: UICollectionViewCell {

    private let imageView: UIImageView

    var sticker: Sticker? {
        willSet {
            if newValue !== sticker {
                imageView.image = nil
            }
        }
        didSet {
            if oldValue !== sticker {
                if let currentSticker = sticker {
                    StickerPackManager.queue.async {
                        let image = currentSticker.imageData.image(withSize: CGSize(width: 256, height: 256))
                        DispatchQueue.main.async {
                            if let sticker = self.sticker, currentSticker === sticker {
                                self.imageView.image = image
                            }
                        }
                    }
                }
            }
        }
    }

    required override init(frame: CGRect) {
        imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        super.init(frame: frame)
        contentView.addSubview(imageView)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        imageView.frame = contentView.bounds
    }

}
