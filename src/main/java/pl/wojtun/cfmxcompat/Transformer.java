package pl.wojtun.cfmxcompat;

/**
 * Created by wojtut01 on 10/11/15.
 */
class Transformer {

  private String m_Key;
  private int m_LFSR_A = 0x13579bdf;
  private int m_LFSR_B = 0x2468ace0;
  private int m_LFSR_C = 0xfdb97531;
  private int m_Mask_A = 0x80000062;
  private int m_Mask_B = 0x40000020;
  private int m_Mask_C = 0x10000002;
  private int m_Rot0_A = 0x7fffffff;
  private int m_Rot0_B = 0x3fffffff;
  private int m_Rot0_C = 0xfffffff;
  private int m_Rot1_A = 0x80000000;
  private int m_Rot1_B = 0xc0000000;
  private int m_Rot1_C = 0xf0000000;

  protected byte[] transformString(String key, byte[] inBytes) {
    setKey(key);
    int length = inBytes.length;
    byte[] outBytes = new byte[length];
    for(int i = 0; i < length; i++) {
      outBytes[i] = transformByte(inBytes[i]);
    }
    return outBytes;
  }

  private byte transformByte(byte target) {
    byte crypto = 0;
    int b = m_LFSR_B & 1;
    int c = m_LFSR_C & 1;
    for(int i = 0; i < 8; i++) {
      if(0 != (m_LFSR_A & 1)) {
        m_LFSR_A = m_LFSR_A ^ m_Mask_A >>> 1 | m_Rot1_A;
        if(0 != (m_LFSR_B & 1)){
          m_LFSR_B = m_LFSR_B ^ m_Mask_B >>> 1 | m_Rot1_B;
          b = 1;
        }
        else{
          m_LFSR_B = m_LFSR_B >>> 1 & m_Rot0_B;
          b = 0;
        }
      }
      else{
        m_LFSR_A = m_LFSR_A >>> 1 & m_Rot0_A;
        if(0 != (m_LFSR_C & 1)) {
          m_LFSR_C = m_LFSR_C ^ m_Mask_C >>> 1 | m_Rot1_C;
          c = 1;
        }
        else{
          m_LFSR_C = m_LFSR_C >>> 1 & m_Rot0_C;
          c = 0;
        }
      }
      crypto = (byte)(crypto << 1 | b ^ c);
    }
    byte out = target;
    out ^= crypto;
    return out;
  }

  private void setKey(String key) {
    int i;
    m_Key = key;
    String localKey = key;
    if(isEmpty(localKey)) localKey = "Default Seed";
    char[] Seed = new char[localKey.length() >= 12 ? localKey.length() : 12];
    m_Key.getChars(0, m_Key.length(), Seed, 0);
    int originalLength = m_Key.length();
    for(i = 0; originalLength + i < 12; i++)
      Seed[originalLength + i] = Seed[i];

    for(i = 0; i < 4; i++) {
      m_LFSR_A = (m_LFSR_A <<= 8) | Seed[i + 4];
      m_LFSR_B = (m_LFSR_B <<= 8) | Seed[i + 4];
      m_LFSR_C = (m_LFSR_C <<= 8) | Seed[i + 4];
    }
    if(0 == m_LFSR_A)m_LFSR_A = 0x13579bdf;
    if(0 == m_LFSR_B)m_LFSR_B = 0x2468ace0;
    if(0 == m_LFSR_C)m_LFSR_C = 0xfdb97531;
  }


  private static boolean isEmpty(String text){
    return text == null || text.length()==0;
  }
}
