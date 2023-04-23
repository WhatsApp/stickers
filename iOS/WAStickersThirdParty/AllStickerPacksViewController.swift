//
// Copyright (c) WhatsApp Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
//

import UIKit

class AllStickerPacksViewController: UIViewController {

    @IBOutlet private weak var stickerPacksTableView: UITableView!

    private var needsFetchStickerPacks = true
    private var stickerPacks: [StickerPack] = []
    private var selectedIndex: IndexPath?

    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 11.0, *) {
            navigationItem.largeTitleDisplayMode = .automatic
        }
        navigationController?.navigationBar.shadowImage = UIImage()
        navigationController?.navigationBar.alpha = 0.0
        stickerPacksTableView.register(UINib(nibName: "StickerPackTableViewCell", bundle: nil), forCellReuseIdentifier: "StickerPackCell")
        stickerPacksTableView.tableFooterView = UIView()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        if let selectedIndex = selectedIndex {
            stickerPacksTableView.deselectRow(at: selectedIndex, animated: true)
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if needsFetchStickerPacks {
            let alert: UIAlertController = UIAlertController(title: "Don't ship this sample app!", message: "If you want to ship your sticker packs to the App Store, create your own app with its own user interface. Your app must have minimum to no resemblance to this sample app.", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: { action in
                self.needsFetchStickerPacks = false
                self.fetchStickerPacks()
            }))
            present(alert, animated: true)
        }
    }

    private func fetchStickerPacks() {
        let loadingAlert = UIAlertController(title: "Loading sticker packs", message: "\n\n", preferredStyle: .alert)
        loadingAlert.addSpinner()
        present(loadingAlert, animated: true)

        do {
            try StickerPackManager.fetchStickerPacks(fromJSON: StickerPackManager.stickersJSON(contentsOfFile: "sticker_packs")) { stickerPacks in
                loadingAlert.dismiss(animated: false) {
                    self.navigationController?.navigationBar.alpha = 1.0

                    if stickerPacks.count > 1 {
                        self.stickerPacks = stickerPacks
                        self.stickerPacksTableView.reloadData()
                    } else {
                        self.show(stickerPack: stickerPacks[0], animated: false)
                    }
                }
            }
        } catch StickerPackError.fileNotFound {
            fatalError("sticker_packs.wasticker not found.")
        } catch {
            fatalError(error.localizedDescription)
        }
    }

    private func show(stickerPack: StickerPack, animated: Bool) {
        // "stickerPackNotAnimated" still animates the push transition on iOS 8 and earlier.
        let identifier = animated ? "stickerPackAnimated" : "stickerPackNotAnimated"
        performSegue(withIdentifier: identifier, sender: stickerPack)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let vc = segue.destination as? StickerPackViewController,
            let stickerPack = sender as? StickerPack {
            vc.title = stickerPack.name
            vc.stickerPack = stickerPack
            vc.navigationItem.hidesBackButton = stickerPacks.count <= 1
        }
    }

    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if let navigationBar = navigationController?.navigationBar {

            let contentInset: UIEdgeInsets = {
                if #available(iOS 11.0, *) {
                    return scrollView.adjustedContentInset
                } else {
                    return scrollView.contentInset
                }
            }()

            if scrollView.contentOffset.y <= -contentInset.top {
                navigationBar.shadowImage = UIImage()
            } else {
                navigationBar.shadowImage = nil
            }
        }
    }

    @objc func addButtonTapped(button: UIButton) {
        let loadingAlert: UIAlertController = UIAlertController(title: "Sending to WhatsApp", message: "\n\n", preferredStyle: .alert)
        loadingAlert.addSpinner()
        present(loadingAlert, animated: true)

        stickerPacks[button.tag].sendToWhatsApp { completed in
            loadingAlert.dismiss(animated: true)
        }
    }
}

// MARK: - UITableViewDelegate

extension AllStickerPacksViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
      selectedIndex = indexPath

      show(stickerPack: stickerPacks[indexPath.row], animated: true)
  }
}

// MARK: - UITableViewDataSource

extension AllStickerPacksViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
      return stickerPacks.count
  }

  func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
      return 100
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
      guard let cell: StickerPackTableViewCell = tableView.dequeueReusableCell(withIdentifier: "StickerPackCell") as? StickerPackTableViewCell else { return UITableViewCell() }
      cell.stickerPack = stickerPacks[indexPath.row]

      let addButton = UIButton(type: .contactAdd)
      addButton.tag = indexPath.row
      addButton.isEnabled = Interoperability.canSend()
      addButton.addTarget(self, action: #selector(addButtonTapped(button:)), for: .touchUpInside)
      cell.accessoryView = addButton

      return cell
  }
}
