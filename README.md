#### [Broadcast SMS_SENT](https://github.com/warren-bank/Android-Broadcast-SMS_SENT)

Android long-running Service that observes outbound SMS messages and broadcasts them for other apps to consume.

#### Specs:

* attributes of the broadcast Intent:
  * action = `android.provider.Telephony.SMS_SENT`
  * extras:
    * `phone`
    * `message`

#### Notes:

* minimum supported version of Android:
  * Android 1.0 (API 1)
* maximum supported version of Android:
  * Android 7.0 (API 24)

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
