//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

class GradientView: UIView {

    override var layer: CAGradientLayer {
        return super.layer as! CAGradientLayer
    }

    convenience init(topColor: UIColor, bottomColor: UIColor) {
        self.init(frame: .zero)

        backgroundColor = .clear

        layer.startPoint = CGPoint(x: 0.5, y: 0.0)
        layer.endPoint = CGPoint(x: 0.5, y: 1.0)

        layer.colors = [topColor.cgColor, bottomColor.cgColor]
    }

    override class var layerClass: AnyClass {
        return CAGradientLayer.self
    }

}
