#! /usr/bin/env python3
#allora dentro la cartella base45-ansi-C-main dovete aprire una shell linux o windows e averci installato GCC
#poi con la shell dovete trovarvi nella cartella base45-ansi-C-main/base45-ansi-C-main (notare sono 2 con nome uguale)
#per capirci meglio se avete la shell linux dovete avere una cosa del tipo giovixo97#GiovanniWin10:/home/giovixo97/base45-ansi-C-main
#scrivete make e date invio e fate fare tutta quella caterba di scritte e poi
#dovete ritrovarvi nella cartella un file base45 senza nessuna estensione
#poi se volete fate sudo mv ./base45 /bin (se volete la vita facile fatelo)
#poi andate dentro la cartella base45-0.4.1/base45-0.4.1 (notare sono 2 con nome uguale) e fare python3 setup.py install
#poi andare con la shell dove c'è il file decode.py e fare python3 decode.py 'risultato della scansione QR code tra apici mi raccomando'
#esempio python3 decode.py 'HC:123456ABCDEF' risultato -> JSON con dati vostri
import json
import sys
import zlib

import base45
import cbor2
from cose.messages import CoseMessage

def main(payload):
    output = ""
    try:
        #payload = sys.argv[1][4:]
        #print("decoding payload: "+ payload)
        # decode Base45 (remove HC1: prefix)
        decoded=base45.b45decode(payload)
        # decompress using zlib
        decompressed=zlib.decompress(decoded)
        # decode COSE message (no signature verification done)
        cose=CoseMessage.decode(decompressed)
        output = ""+json.dumps(cbor2.loads(cose.payload), indent = 2)
    except:
        output = "error"

    return output
#print(json.dumps(cbor2.loads(cose.payload), indent=2))