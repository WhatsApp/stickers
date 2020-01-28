//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

final class StickerPackTableViewCell: UITableViewCell, UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout {

    private let cellLength: CGFloat = 48
    private let interimSpacing: CGFloat = 10

    @IBOutlet private weak var stickerPackTitleLabel: UILabel!
    @IBOutlet private weak var stickerPackDescriptionLabel: UILabel!
    @IBOutlet private weak var stickerPackCollectionView: UICollectionView!

    var stickerPack: StickerPack? {
        didSet {
            stickerPackTitle = stickerPack?.name
            stickerPackSecondaryInfo = stickerPack?.publisher
            stickerPackCollectionView.reloadData()
        }
    }

    var stickerPackTitle: String? {
        get {
            return stickerPackTitleLabel.text
        }
        set {
            stickerPackTitleLabel.text = newValue
        }
    }

    var stickerPackSecondaryInfo: String? {
        get {
            return stickerPackDescriptionLabel.text!
        }
        set {
            stickerPackDescriptionLabel.text = newValue
        }
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        stickerPackCollectionView.dataSource = self
        stickerPackCollectionView.delegate = self
        stickerPackCollectionView.register(StickerCell.self, forCellWithReuseIdentifier: "StickerCell")
    }

    // MARK: Collectionview

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return interimSpacing;
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: cellLength, height: cellLength)
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        guard let stickerPack = stickerPack else { return 0 }

        return stickerPack.stickers.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "StickerCell", for: indexPath) as! StickerCell
        if let sticker = stickerPack?.stickers[indexPath.row] {
            cell.sticker = sticker
        }
        return cell
    }
}
