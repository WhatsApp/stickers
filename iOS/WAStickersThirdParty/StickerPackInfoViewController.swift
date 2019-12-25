//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

private enum Website {
    case publisher
    case privacyPolicy
    case licenseAgreement
}

class StickerPackInfoViewController: UITableViewController {

    private static let footerString: String = """
    After adding this sticker pack to WhatsApp, you will be able to send these stickers to anyone \
    in WhatsApp. To delete the sticker pack, go to the "My Stickers" panel in WhatsApp.
    """

    private var footerHeight: CGFloat {
        var insets: UIEdgeInsets = .zero
        if #available(iOS 11.0, *) {
            insets = tableView.safeAreaInsets
        }
        footerView.label.preferredMaxLayoutWidth = footerView.preferredLabelLayoutWidth(viewWidth: tableView.bounds.size.width - FooterView.MARGIN * 2 - insets.left - insets.right)
        return ceil(footerView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).height)
    }

    var stickerPack: StickerPack!

    private var websiteIndexes: [Website: Int] = [:]

    private func setupWebsites() {
        var index: Int = 3

        if stickerPack.publisherWebsite != nil {
            websiteIndexes[.publisher] = index
            index += 1
        }

        if stickerPack.privacyPolicyWebsite != nil {
            websiteIndexes[.privacyPolicy] = index
            index += 1
        }

        if stickerPack.licenseAgreementWebsite != nil {
            websiteIndexes[.licenseAgreement] = index
            index += 1
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setupWebsites()

        tableView.register(UINib(nibName: "SecondaryTextTableViewCell", bundle: nil), forCellReuseIdentifier: "SecondaryCell")
        tableView.register(UINib(nibName: "LinkTableViewCell", bundle: nil), forCellReuseIdentifier: "LinkCell")
    }
    
    @IBAction func donePressed(_ sender: UIBarButtonItem) {
        dismiss(animated: true)
    }

    var footerView: FooterView = FooterView(frame: CGRect.zero)

    class FooterView: UIView {

        static let MARGIN: CGFloat = 8
        let label: UILabel

        override init(frame: CGRect) {
            label = UILabel()
            super.init(frame: frame)
            backgroundColor = .clear
            label.translatesAutoresizingMaskIntoConstraints = false;
            label.numberOfLines = 0
            label.lineBreakMode = .byWordWrapping
            label.font = .systemFont(ofSize: 14)
            label.text = footerString
            label.textColor = .gray
            label.setContentHuggingPriority(.defaultHigh, for: .horizontal)
            label.setContentHuggingPriority(.defaultHigh, for: .vertical)
            label.setContentCompressionResistancePriority(.required, for: .horizontal)
            label.setContentCompressionResistancePriority(.required, for: .vertical)
            addSubview(label)
            
            // Add constraints
            
            addConstraint(NSLayoutConstraint(item: label, attribute: .top, relatedBy: .equal, toItem: self, attribute: .top, multiplier: 1.0, constant: FooterView.MARGIN))
            addConstraint(NSLayoutConstraint(item: label, attribute: .leading, relatedBy: .equal, toItem: self, attribute: .leadingMargin, multiplier: 1.0, constant: FooterView.MARGIN))
            addConstraint(NSLayoutConstraint(item: label, attribute: .trailing, relatedBy: .equal, toItem: self, attribute: .trailingMargin, multiplier: 1.0, constant: -FooterView.MARGIN))
            addConstraint(NSLayoutConstraint(item: label, attribute: .bottom, relatedBy: .equal, toItem: self, attribute: .bottomMargin, multiplier: 1.0, constant: FooterView.MARGIN))
        }

        required init?(coder aDecoder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }

        func preferredLabelLayoutWidth(viewWidth: CGFloat) -> CGFloat {
            return viewWidth - 2 * FooterView.MARGIN
        }
    }

    // MARK: Tableview

    private func index(ofWebsite website: Website) -> Int {
        return (websiteIndexes[website] == nil ? -1 : websiteIndexes[website]!)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 3 + websiteIndexes.count
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }

    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return footerView
    }

    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return footerHeight
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: UITableViewCell!

        if indexPath.row <= 2 {
            cell = tableView.dequeueReusableCell(withIdentifier: "SecondaryCell")
            let secondaryCell = cell as! SecondaryTextTableViewCell

            switch indexPath.row {
            case 0:
                secondaryCell.primaryImage = stickerPack.trayImage.image
                secondaryCell.secondaryText = "Tray Icon"
            case 1:
                secondaryCell.primaryText = stickerPack.publisher
                secondaryCell.secondaryText = "Author"
            case 2:
                secondaryCell.primaryText = stickerPack.formattedSize
                secondaryCell.secondaryText = "Size"
            default:
                break
            }
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: "LinkCell")
            let linkCell = cell as! LinkTableViewCell

            switch indexPath.row {
            case index(ofWebsite: .publisher):
                linkCell.linkTitle = "Publisher Website"
                linkCell.linkImage = UIImage(named: "WAWebsiteIcon")!.withRenderingMode(.alwaysTemplate)
            case index(ofWebsite: .privacyPolicy):
                linkCell.linkTitle = "Privacy Policy"
                linkCell.linkImage = UIImage(named: "WAPrivacyPolicyIcon")!.withRenderingMode(.alwaysTemplate)
            case index(ofWebsite: .licenseAgreement):
                linkCell.linkTitle = "License Agreement"
                linkCell.linkImage = UIImage(named: "WALicenseIcon")!.withRenderingMode(.alwaysTemplate)
            default:
                break
            }
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, willSelectRowAt indexPath: IndexPath) -> IndexPath? {
        if indexPath.row <= 2 {
            return nil
        }

        return indexPath
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)

        var urlToOpen: URL?

        switch indexPath.row {
        case index(ofWebsite: .publisher):
            urlToOpen = URL(string: stickerPack.publisherWebsite!)
        case index(ofWebsite: .privacyPolicy):
            urlToOpen = URL(string: stickerPack.privacyPolicyWebsite!)
        case index(ofWebsite: .licenseAgreement):
            urlToOpen = URL(string: stickerPack.licenseAgreementWebsite!)
        default:
            break
        }

        if let websiteURL = urlToOpen {
            if UIApplication.shared.canOpenURL(websiteURL) {
                if #available(iOS 10.0, *) {
                    UIApplication.shared.open(websiteURL)
                } else {
                    UIApplication.shared.openURL(websiteURL)
                }
            }
        }
    }

    override func tableView(_ tableView: UITableView, shouldHighlightRowAt indexPath: IndexPath) -> Bool {
        return indexPath.row > 2
    }

}
