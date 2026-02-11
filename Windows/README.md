# Windows

---

## Windows/README.md

```md
# Windows (CFS-RFID)

WinForms app for reading/writing Creality CFS-compatible RFID tags.

## Requirements

- Windows 10/11
- NFC reader with PC/SC support  
  **Tested with:** ACR122U
- MIFARE Classic 1K tags (13.56MHz)

## Download

If you just want to run it:
- Use the prebuilt binaries from this repo’s **Releases** page.

If you want to build it:
- See **Build from source** below.

## Build from source (Visual Studio 2022)

1. Install Visual Studio 2022 workload: **.NET desktop development**
2. Install **.NET Framework 4.8.1 developer/targeting pack**
3. Open: `Windows/CFS-RFID.sln`
4. Build configuration: **Release**
5. Build Solution

Output will be under something like:
- `Windows/CFS-RFID/bin/Release/`

## Spoolman integration (this fork)

### What it does

When enabled, the app can:

1. Create a spool in **Spoolman** (REST API)
2. Store the returned **Spoolman `spool.id`** in the Creality tag’s `reserve` field:
   - decimal, zero‑padded to 6 digits (e.g. `000005`)
3. After writing a tag, write the tag UID back into the Spoolman spool comment:
   - `[CFS-RFID] tag_uid=80BFA83A`

This creates a two-way cross-reference:
- Tag → reserve → Spoolman spool.id
- Spoolman spool → comment → tag UID(s)

### Intended workflow

1. Select filament settings (material / color / spool weight)
2. Click **Add (Spoolman)**  
   - Creates spool in Spoolman
   - Queues `reserve (Spoolman spool ID): 000005`
3. Click **Write Tag**  
   - Writes reserve to the tag
   - Writes tag UID back into Spoolman

Write **two tags per spool** for Creality CFS (one on each side), and use **Write Tag** twice.
The queued spool id remains active until filament/spool selection changes (and resets on app restart).

### Filament matching strategy

If your Spoolman filaments are seeded from Creality materials, matching is most reliable when:

- `filament.article_number = "Creality:<MaterialID>"`

The Windows app can prefer this mapping to avoid duplicate filaments.

## Hidden settings (Registry)

Path:
`HKCU\CFS RFID\Settings`

Keys:

- `SmWriteReserve` (DWORD, default `1`)  
  If `1`, store Spoolman spool ID into tag reserve on Write Tag.

- `SmWriteTagUidBack` (DWORD, default `1`)  
  If `1`, append `[CFS-RFID] tag_uid=...` to Spoolman spool comment after a successful write.

- `SmArticlePrefix` (String, default `Creality:`)  
  Prefix used when matching Spoolman filament `article_number`.

- `SmReserveHex` (DWORD, default `0`)  
  If `1`, encode reserve as hex. Default is decimal (`0`).

## Troubleshooting quick hits

- **Reader not detected:** make sure Windows smart card / PCSC stack is working and the reader shows up in Device Manager.
- **Spoolman creates duplicates:** ensure your Spoolman filament list is seeded consistently (prefer article_number mapping).
- **Printer doesn’t “see” the tag in CFS:** CFS is picky about tag placement; two tags per spool (one per side) is recommended.

<br>

This was tested using the <a href=https://www.acs.com.hk/en/products/3/acr122u-usb-nfc-reader/>ACR122U</a> reader.
<br><br>
A compiled copy can be downloaded from the <a href=https://github.com/DnG-Crafts/K2-RFID/releases>releases</a> page.
<br><br><br>
Compiled with <a href=https://visualstudio.microsoft.com/vs/community/>Visual Studio</a>



<br><br>

<img src=https://github.com/DnG-Crafts/K2-RFID/blob/main/Windows/winapp.jpg>
