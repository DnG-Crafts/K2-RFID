#ifndef __AES_H__
#define __AES_H__

#if defined(AVR)
#include <avr/pgmspace.h>
#else
#include <pgmspace.h>
#endif

typedef unsigned char byte;

class AES
{
public:
  byte set_key(byte key[]);
  byte encrypt(byte plain[16], byte cipher[16]);

private:
  byte key_sched[240];
};

#endif
