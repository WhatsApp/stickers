#  iOS Stickers Apps for WhatsApp

## PLEASE READ: IMPORTANT NOTICE

With Apple’s strict App Store review policy, we recommend iOS developers to submit apps that contain more functionality than to simply export stickers. This will increase the chances of the app being accepted. As an alternative to creating an iOS app, there are various sticker maker apps that help you create and import stickers into WhatsApp.

The sample iOS app is an example on how to use our API to import stickers into WhatsApp, and can serve as guide on how to get started, but it is not meant to be used as template for your app because it will not be accepted by Apple.

## Overview
If you would like to design your own stickers for WhatsApp, you can package them in an iOS app. You will need to distribute your app via the App Store. Users who download and install your sticker app will be able to add your stickers to their WhatsApp sticker picker/tray, and start sending those stickers from within WhatsApp. A separate app is necessary and it will reside on your phone's home screen just like any other app. Once you add the stickers from the app to WhatsApp, you can remove or uninstall the app from your phone and continue to send those stickers. Stickers on WhatsApp must be legal, authorized, and acceptable.  Learn more about acceptable use of our services at <https://www.whatsapp.com/legal/#terms-of-service>.

The sample code provides a way for you to understand how the API works. You can also drop in your assets to test if they work correctly with WhatsApp. Importantly, you *must* make sure to develop a unique user interface with your own styling to comply with Apple's App Store guidelines. Do not use our sample app's UI as is. You must significantly modify the UI before submitting your app to Apple.

For a description of how the API works, refer to the [API documentation](#API-documentation) section below.

We recommend you create a version of your sticker app for Android as well to give users of WhatsApp on Android an opportunity to download your sticker app as well.

## Sticker art and app requirements
We recommend you refer to the FAQ at https://faq.whatsapp.com/general/26000226 for complete details on how to properly create your sticker art. This FAQ also contains a sample PSD that demonstrates best practices for composing legible, rich sticker art.

* A sticker is an image that has a transparent background and can be sent in a
WhatsApp chat
* Stickers are organized into "packs". Users must explicitly add each pack to WhatsApp one-by-one, so your app should list each pack separately and each pack must have its own affordance to add it to WhatsApp (do not try to create "add all packs" operations).
* Each sticker pack must have a minimum of 3 stickers and a maximum of 30 stickers
* Stickers must be exactly 512 x 512 pixels
* Stickers will render on a variety of backgrounds, white, black, colored, patterned, etc. Test your sticker art on a variety of backgrounds. For this reason, we recommend you add a 8px #FFFFFF stroke to the outside of each sticker. See the sample PSD referenced at https://faq.whatsapp.com/general/26000226 for more details. 
* Stickers must be either in PNG or the [WebP format](https://developers.google.com/speed/webp). Currently, animated WebP, animated PNG, or animated stickers of any kind are not supported. See the section [Converting to WebP](#converting-to-webp) below for information on how to create WebP files.
* Each sticker must be less than 100KB. See the section [Tips for Reducing File Size](#tips-for-reducing-file-size) below. 
* Sticker Picker/Tray Icon
* Provide an image that will be used to represent your sticker pack in the WhatsApp sticker picker/tray 
* This image should be 96 x 96 pixels
* Max file size of 50KB

### Tips for reducing file size
We recommend reducing the size of each of your stickers. For reference, many of the stickers provided within WhatsApp are around 15KB each. The smaller your stickers, the faster they will send and the less data your users will have to spend sending them or downloading your app. Depending on your art and the type of graphics you've created, one of these two methods may result in smaller file sizes so we recommend experimenting with both. 

* The first method involves optimizing your PNGs using a PNG optimizer tool. If you're on MacOS, use https://pngmini.com. We recommend using Median Cut and adjusting the colors bar to reduce the size. Then, convert them to WebP using the methods described in the [Converting to WebP](#converting-to-webp) section.
* The second method involves saving or converting your stickers as WebP while experimenting with the WebP export settings trying to optimize the  images. You should try setting the quality of your WebP output to something lower than 100% and experiment with a quality level that gets you the smallest file size possible without noticeable image degradation. EEach of the methods described in [Converting to WebP](#converting-to-webp) for exporting your files to WebP offer ways to control your resolution.

### Converting to WebP
It is recommended to use the WebP format for your sticker art. We recommend using the tools you're most comfortable with to draw and compose your art, and converting them to WebP using one of a few different tools:

* Sketch for Mac lets you export files as WebP. Open your sticker art file in Sketch, select a layer, multiple layers, or an artboard, and select Make Exportable in the bottom right. Pick your format as WebP, select Export, and then select the quality/resolution.
* [Android Studio](https://developer.android.com/studio/) allows you to convert PNGs to WebP. Simply create a New Project in Android Studio, open your PNG and right click on the image and select convert to WebP ([https://developer.android.com/studio/write/convert-webp](https://developer.android.com/studio/write/convert-webp)). Make sure you uncheck the box next to "Skip images with transparency/alpha channel" in the export flow.
* You can install a [plugin](https://github.com/fnordware/AdobeWebM#download) for Photoshop that converts to WebP. Make sure to uncheck the "Save Metadata" checkbox.
* Use [cwebp](https://developers.google.com/speed/webp/), a command line tool
* Use [squoosh](https://squoosh.app/), an online browser tool, by the Google Chrome Labs

### Important note about using PNG images
Xcode does some optimizations to every PNG image that is imported. This can lead to files becoming bigger in size and not passing our image size requirement. To prevent this, select the PNG file from the left sidebar and go to the right sidebar and select the first tab (to the left of the question mark button). On the "Type" drop down menu, select "Data". Do this for all of your PNG images (WebP images don't need this treatment).

## How to use the sample app

### Overview
Follow these steps if you want to test your sticker assets with the sample app.

* After downloading this repo, open the `WAStickersThirdParty.xcodeproj` file with [Xcode](https://itunes.apple.com/us/app/xcode/id497799835).
* Remove the current sample stickers from the `SampleStickers` folder in Xcode
* Drag your sticker images inside the `SampleStickers` folder in Xcode
* A window appears every time you drag an image into Xcode. Make sure that "Copy items if needed" and the `WAStickersThirdParty` target are checked.

### Modifying the sticker_packs.wasticker file
In Xcode, you must also modify the 'sticker_packs.wasticker' file. Replace the values of the metadata with your own. A few notes: 

* `name`: the sticker pack's name (128 characters max)
* `identifier`: The identifier should be unique and can be alphanumeric: a-z, A-Z, 0-9, and the following characters are also allowed "_", "-", "." and " ". The identifier should be less than 128 characters.
* Replace the `image_file` value with the file name of your sticker image. It must have both the file name and extension. The ordering of the files in the JSON will dictate the ordering of your stickers in your pack. 
* `ios_app_store_link` and `android_play_store_link` (optional fields): here you can put the URL to your sticker app in the App Store as well as a URL to your sticker app in the Google Play Store (if you have an Android version of your sticker app). If you provide these URLs, users who receive a sticker from your app in WhatsApp can tap on it to view your sticker app in the respective App Stores. To get your App Store link before you publish your app, refer to the instructions here: https://stackoverflow.com/questions/4137426/get-itunes-link-for-app-before-submitting.
* `emojis` (optional): add up to a maximum of three emoji for each sticker file. Select emoji that best describe or represent that sticker file. For example, if the sticker is portraying love, you may choose to add a heart emoji like 💕. If your sticker portrays pizza, you may want to add the pizza slice emoji 🍕. In the future, WhatsApp will support a search function for stickers and tagging your sticker files with emoji will enable that. The sticker picker/tray in WhatsApp today already categorizes stickers into emotion categories (love, happy, sad, and angry) and it does this based on the emoji you tag your stickers with. 

The following fields are optional: `ios_app_store_link`, `android_play_store_link`, `publisher_website`, `privacy_policy_website`, `license_agreement_website`, `emoji`

If your app has more than 1 sticker pack, you will need to reference it in `sticker_packs.wasticker`. Simply create a second array within the "sticker_packs" section of the file and include all the metadata (name, identifier, etc) along with all the references to the sticker files. 

### Build the sample app
Make sure to run and test your sticker app. You can use the simulator to run the app, but you need to install your app on an actual iPhone to test and see if it works with WhatsApp. 

## Submit your app
To submit your app to the App Store, you'll need to enroll as an Apple Developer at [https://developer.apple.com/programs/enroll/](https://developer.apple.com/programs/enroll/). 

Then, follow the instructions for publishing your app to the App Store: [https://help.apple.com/xcode/mac/current/#/dev442d7f2ca](https://help.apple.com/xcode/mac/current/#/dev442d7f2ca).

When preparing your app for submission in iTunes Connect, you'll have the option to add keywords associated with your app. WhatsApp can launch the App Store and perform a search for other sticker pack apps. To make sure that your app is shown in this list, add the keyword `WAStickers` when setting up your app in App Store connect. You can use additional keywords, but make sure you at least use this one.

### Required before you publish your app
Your bundle identifier cannot use anything that includes `WA.WAStickersThirdParty`. This is the only restriction on what you put as a bundle ID.

To change the bundle identifier:

* On the left sidebar in Xcode, select the very first item (unless you changed the project name, it should be `WAStickersThirdParty`)
* Change the field which says "Bundle Identifier"

To change the app name:

* On the left sidebar in Xcode, tap on the very first item (unless you changed the project name, it should be `WAStickersThirdParty`)
* Change the field which says "Display Name"

To change your app's icon:

* On the left sidebar in Xcode, tap on `Assets.xcassets`
* You'll see a lot of sections. The sections of our interest are "iPhone App" and "App Store" (required for publishing to the App Store).
* The 2x icon is 120x120 pixels and the 3x icon is 180x180. Just drag your images into the corresponding 2x or 3x bucket. If you have a 1024x1024 image for the App Store, drag it into the "App Store" bucket.

## API documentation
We provide built-in helper classes and methods to easily create objects to represent your stickers and sticker packs, and to send them to the WhatsApp app.

### Files to use
Copy the following files from the sample app into your Xcode project:
* `Limits.swift`
* `StickerPackManager.swift`
* `StickerPack.swift`
* `Sticker.swift`
* `ImageData.swift`
* `Interoperability.swift`
* `WebPManager.swift`
* All of the files that have the "YY" prefix.

Please remember to create the Bridging Header file (if you don't have one already), and to add `#import "YYImage.h"`

You will also need to add the `WebP.framework` to your Linked Frameworks and Libraries.

### Create a sticker pack
To create a sticker pack to be sent to WhatsApp, instantiate a new `StickerPack` object:
```
let stickerPack = StickerPack(identifier: "identifier",
name: "sticker pack name", 
publisher: "sticker pack publisher", 
trayImageFileName: "tray image file name", 
publisherWebsite: "publisher website URL", 
privacyPolicyWebsite: "privacy policy website URL", 
licenseAgreementWebsite: "license agreement website URL")
```

Note: this method will throw an exception when any of the parameters don’t meet the requirements described in the sections above.

Alternatively, there is another initializer in `StickerPack` that accepts raw PNG image data instead of a file name for the sticker tray image.

### Add stickers to a sticker pack
Next, run this method for each sticker you want to add:
```
stickerPack.addSticker(contentsOfFile: "file name of sticker image", 
emojis: ["array of emojis"])
```

### Importing your sticker pack to WhatsApp
To import your sticker pack on WhatsApp, call the method below. This will open the WhatsApp app with a preview of your pack, and the user will be presented the option to save it on their sticker picker. 
```
stickerPack.sendToWhatsApp { completed in
// Called when the sticker pack has been wrapped in a form that WhatsApp 
// can read and WhatsApp is about to open.
}
```

### Structure of the JSON file that is sent to WhatsApp
If you don't want to use the API described above, you need to know how the data is sent and its structure. 

To communicate with WhatsApp, you must copy your sticker data into the pasteboard first. See [UIPasteboard](https://developer.apple.com/documentation/uikit/uipasteboard) for more. Then you need to open WhatsApp through the URL scheme `whatsapp://stickerPack`. WhatsApp will then grab your stickers from the pasteboard. 

In order for your app to to be able to check if it can open a URL using the `whatsapp://` scheme, you'll need to add the URL scheme in your `Info.plist` like this:

```
<key>LSApplicationQueriesSchemes</key>
	<array>
		<string>whatsapp</string>
	</array>
```

Format your sticker data into a JSON object with the structure described below. Then convert it into a Data object before putting it in the pasteboard.

```
{
  "ios_app_store_link" : "String",
  "android_play_store_link" : "String",
  "identifier" : "String",
  "name" : "String",
  "publisher" : "String",
  "tray_image" : "String", (Base64 representation of the PNG, not WebP, data of the tray image)
  "stickers" : [
    {
      "image_data" : "String", (Base64 representation of the WebP, not PNG, data of the sticker image)
      "emojis" : ["String", "String"] (Array of emoji strings. Maximum of 3 emoji)
    }
  ]
}
```

Important notes:
* `tray_image` uses PNG data whereas `image_data` uses WebP data.
* You can only send one sticker pack at a time
