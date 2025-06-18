# ⚡ QuickSE

> 🔐 Instantly check and toggle SELinux status on rooted Android devices.

![GitHub stars](https://img.shields.io/github/stars/maazm7d/QuickSE?style=for-the-badge)
![GitHub forks](https://img.shields.io/github/forks/maazm7d/QuickSE?style=for-the-badge)
![GitHub license](https://img.shields.io/github/license/maazm7d/QuickSE?style=for-the-badge)

---

## 📱 What is QuickSE?

**QuickSE** is a lightweight, open-source Android app that lets rooted users **view**, **toggle**, and **auto-set SELinux mode** between **Enforcing** and **Permissive**, all in a modern Material You interface.  
Built using **Jetpack Compose** and Kotlin, it offers a fast and intuitive experience with just the essential tools you need .

---

## ⚙️ Requirements

- 📱 Android 7.0+ (API 24 or higher)
- 🔓 Root access 

---

## ⚙️ How QuickSE Works

QuickSE uses root access to control SELinux on your Android device:

- 🧩 **Check Status:** Runs `getenforce` to read the current SELinux mode.
- 🔁 **Toggle Mode:** Executes `setenforce 0` or `setenforce 1` to switch between Permissive and Enforcing.
- 🧠 **Built with Jetpack Compose:** Offers a modern, responsive UI with Material You styling.

> ⚠️ Requires root access. SELinux mode resets on reboot unless re-applied.

---

## ⬇️  Download
- [**Download APK**](https://github.com/maazm7d/QuickSE/releases)
 
---

## 🤝 Contributing

Pull requests are welcome! For major changes, open an issue first to discuss what you'd like to change.

Feel free to fork and submit PRs for:

- Bug fixes
- UI improvements
- New features

---


## 📄 License

Licensed under the [GNU GPLv3](LICENSE).
You are free to use, modify, and distribute this app under the terms of the license.

---


## 📬 Contact / Support

Have questions or suggestions? Reach out on Telegram: [@maazm7d](https://t.me/maazm7d)
