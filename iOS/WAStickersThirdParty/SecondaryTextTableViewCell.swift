//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

class SecondaryTextTableViewCell: UITableViewCell {

    @IBOutlet private weak var leftLabel: UILabel!
    @IBOutlet private weak var rightLabel: UILabel!
    @IBOutlet private weak var rightImageView: UIImageView!

    var primaryText: String? {
        get {
            return rightLabel.text
        }

        set {
            rightLabel.text = newValue
            rightImageView.image = nil
        }
    }

    var secondaryText: String? {
        get {
            return leftLabel.text
        }

        set {
            leftLabel.text = newValue
        }
    }

    var primaryImage: UIImage? {
        get {
            return rightImageView.image
        }

        set {
            rightImageView.image = newValue;
            rightLabel.text = nil
        }
    }
}
