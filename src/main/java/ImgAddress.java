/**
 * Enum for currently programmed addresses of the playing card emotes.
 */

public enum ImgAddress {
    b2("<:b2:741730954820714538>"),
    b3("<:b3:741730954992812193>"),
    b4("<:b4:741730954644815884>"),
    b5("<:b5:741730955089281064>"),
    b6("<:b6:741730955055595640>"),
    b7("<:b7:741730954741284946>"),
    b8("<:b8:741730955135549470>"),
    b9("<:b9:741730955181424660>"),
    bT("<:bT:741730955701780552>"),
    bJ("<:bJ:741730955181555824>"),
    bQ("<:bQ:741730954900668438>"),
    bK("<:bK:741730955189813288>"),
    bA("<:bA:741730954774577213>"),

    r2("<:r2:741730955806638080>"),
    r3("<:r3:741730954955063358>"),
    r4("<:r4:741730955512774777>"),
    r5("<:r5:741730955475157042>"),
    r6("<:r6:741730955265310761>"),
    r7("<:r7:741730955538202704>"),
    r8("<:r8:741730955420500078>"),
    r9("<:r9:741730955387207872>"),
    rT("<:rT:741730955055595571>"),
    rJ("<:rJ:741730955659837559>"),
    rQ("<:rQ:741730955580014643>"),
    rK("<:rK:741730955559043102>"),
    rA("<:rA:741730955101863937>"),

    sS("<:sS:741730955772821514>"),
    sH("<:sH:741730955265441792>"),
    sD("<:sD:741730954904600716>"),
    sC("<:sC:741730955307253801>"),
    sN("<:sN:742565475233300531>");

    private final String address;

    /**
     * ImgAddress constructor.
     * @param address The Discord address of the requested emote.
     */
    ImgAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the Discord address of the requested emote.
     * @return A String containing the Discord address of the requested emote.
     */
    public String getAddress() {
        return this.address;
    }
}

