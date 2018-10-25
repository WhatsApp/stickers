//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

class LinkTableViewCell: UITableViewCell {

    @IBOutlet private weak var linkLabel: UILabel!
    @IBOutlet private weak var linkImageView: UIImageView!

    var link: String?

    var linkTitle: String? {
        get {
            return linkLabel.text
        }

        set {
            linkLabel.text = newValue
        }
    }

    var linkImage: UIImage? {
        get {
            return linkImageView.image
        }

        set {
            linkImageView.image = newValue
        }
    }

}
