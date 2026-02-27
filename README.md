# CFS-RFID
K2/K1/HI/CFS RFID Programming.<br>

Tools for programming **Creality K2 / K1 / Hi / CFS** compatible **RFID tags**.

This repo is a fork of **DnG-Crafts/K2-RFID** (credit to the original author(s) for the reverse‑engineering and tooling).

> **Tags required:** MIFARE Classic 1K (13.56MHz).

## What’s in this repo

- **Windows/** — WinForms app (“CFS-RFID”) to read/write tags (this fork adds Spoolman integration)
- **Android/** — Android app source
- **Arduino/** — Arduino/ESP/Pico examples
- **db/**, **docs/** — supporting data and docs

## What this fork adds (pickmanmike)

This fork adds an optional **Spoolman** integration to the Windows app to create a *single stable identifier* across:

- the **physical spool** (RFID tags)
- the **printer’s parsed RFID state**
- the **Spoolman database**

### Key idea

We use the Creality tag’s **serialNum** field to store:

> `serialNum` = **Spoolman `spool.id`** (decimal, zero‑padded to 6 digits)

Legacy `reserve` defaults to `000000` on new tags.

Example: `000005`

This value is read by the printer and is observable in:

`/mnt/UDISK/creality/userdata/box/material_box_info.json`

### Windows workflow (Spoolman + tags)

1. Select filament settings in the Windows app (material, color, spool weight, etc.).
2. Click **Add (Spoolman)**:
   - creates a **Spool** in Spoolman via REST API
   - captures the returned `spool.id`
   - queues that ID in-memory for writing to tags
3. Click **Write Tag**:
   - writes the queued `spool.id` into the tag `serialNum` field
   - writes **tag UID(s)** back into Spoolman `spool.extra` (two-way breadcrumb)
4. For Creality CFS usage, write **two tags per spool** (one on each side).

**Queue behavior (by design):**
- The queued Spoolman ID stays active for *multiple* tag writes (for “two tags per spool” + retries).
- The queued ID is cleared when filament/spool selection changes.
- The queued ID is NOT persisted to disk (closing the app is a “start over” reset).

### Spoolman mapping / de-duplication

If you seed Spoolman filaments from Creality’s material database, this fork can match filaments using:

- `filament.article_number = "Creality:<MaterialID>"`

(Exact matching behavior depends on how your Spoolman is populated; see Windows README for details.)

## Verify serialNum on the printer

SSH to the printer and run:

```sh
grep -n "000005" /mnt/UDISK/creality/userdata/box/material_box_info.json || echo "not found"

Expected output should include something like:

"serialNum":"000005"

Tag format (Creality)

The tag data includes the following high-level fields:
date	vendorId	batch	filamentId	color	filamentLen	serialNum	reserve

    This fork uses the serialNum field to store the Spoolman spool ID. 

Files of interest on the printer

Common Creality Hi / K2 locations:

    /mnt/UDISK/creality/userdata/box/tn_data.json

    /mnt/UDISK/creality/userdata/box/material_box_info.json

    /mnt/UDISK/creality/userdata/box/material_database.json

    /mnt/UDISK/creality/userdata/box/material_modify_info.json


Windows app

See: Windows/README.md


The tags required are <a href=https://en.wikipedia.org/wiki/MIFARE>MIFARE</a> Classic 1k tags.<br>


<br>
<a href=https://github.com/DnG-Crafts/K2-RFID/tree/main/Android/SpoolID>Android Code</a>
<br>
<br>

<a href=https://github.com/DnG-Crafts/K2-RFID/tree/main/Arduino>Arduino Code</a>
<br>
<br>

<a href=https://github.com/DnG-Crafts/K2-RFID/tree/main/Windows>Windows Code</a>
<br>
<br>

[![https://www.youtube.com/watch?v=6EA4t7zgq90](https://img.youtube.com/vi/6EA4t7zgq90/0.jpg)](https://www.youtube.com/watch?v=6EA4t7zgq90)

https://www.youtube.com/watch?v=6EA4t7zgq90<br>


<br><br><br>
The android app is available on google play<br>
<a href="https://play.google.com/store/apps/details?id=dngsoftware.spoolid&hl=en"><img src=https://github.com/DnG-Crafts/K2-RFID/blob/main/docs/gp.webp width="30%" height="30%"></a>
<br>



## Tag Format
```
Creality RFID Tag Data

 AB1240276A21010010FFFFFF0165000001000000
 AB1240276A21010010C12E1F0165000001000000
 9A2240276A210100100000000165000001000000
 AB1240276A21010010C12E1F0165000001000000
 
    date             
|  M DD YY  | venderId | batch | filamentId |  color  | filamentLen | serialNum | reserve |
|           |          |       |            |         |             |           |         |
|  A B1 24  |   0276   |  A2   |   101001   | 0FFFFFF |    0165     |   000001  |  000000 |
|           |          |       |            |         |             |           |         |
|  A B1 24  |   0276   |  A2   |   101001   | 0C12E1F |    0165     |   000001  |  000000 |
|           |          |       |            |         |             |           |         |
|  9 A2 24  |   0276   |  A2   |   101001   | 0000000 |    0165     |   000001  |  000000 |
|           |          |       |            |         |             |           |         |
|  A B1 24  |   0276   |  A2   |   101001   | 0C12E1F |    0165     |   000001  |  000000 |
  
```

<img src=https://github.com/DnG-Crafts/K2-RFID/blob/main/docs/ghi.jpg width=50% height=50%>




## Files of interest
```
 /mnt/UDISK/creality/userdata/box/tn_data.json
 /mnt/UDISK/creality/userdata/box/material_box_info.json
 /mnt/UDISK/creality/userdata/box/material_database.json
 /mnt/UDISK/creality/userdata/box/material_modify_info.json
```


# Arduino

<a href=https://github.com/DnG-Crafts/K2-RFID/tree/main/Arduino/ESP32>Code for ESP32 boards</a><br>
<a href=https://github.com/DnG-Crafts/K2-RFID/tree/main/Arduino/ESP8266>Code for ESP8266 boards</a><br>
<a href=https://github.com/DnG-Crafts/K2-RFID/tree/main/Arduino/Pico_W>Code for Pico W boards</a><br>

<br><br>

Flash Code:

[![https://www.youtube.com/watch?v=WOznbT7NbWI](https://img.youtube.com/vi/WOznbT7NbWI/0.jpg)](https://www.youtube.com/watch?v=WOznbT7NbWI)

https://www.youtube.com/watch?v=WOznbT7NbWI<br>


Credits / upstream

    Upstream repo: DnG-Crafts/K2-RFID

    This fork: pickmanmike/K2-RFID
