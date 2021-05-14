package be.ac.ulb.infof307.g06.models.cloudModels.DropBox;

import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author Kannan Goundan
 * https://github.com/dropbox/dropbox-api-content-hasher/blob/master/java/src/DropboxContentHasher.java
 */

public final class DropBoxContentHasher extends MessageDigest implements Cloneable {
    private MessageDigest overallHasher;
    private MessageDigest blockHasher;
    private static final int BLOCK_SIZE = 4 * 1024 * 1024;
    private int blockPos;

    public DropBoxContentHasher() {
        this(newSha256Hasher(), newSha256Hasher(), 0);
    }

    private DropBoxContentHasher(MessageDigest overallHasher, MessageDigest blockHasher, int blockPos) {
        super("Dropbox-Content-Hash");
        setOverallHasher(overallHasher);
        setBlockHasher(blockHasher);
        setBlockPos(blockPos);
    }

    private static int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    protected void engineUpdate(byte input) {
        finishBlockIfFull();

        getBlockHasher().update(input);
        setBlockPos(getBlockPos() + 1);
    }

    @Override
    protected int engineGetDigestLength() {
        return getOverallHasher().getDigestLength();
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len) {
        int inputEnd = offset + len;
        int newOffset = offset;
        while (newOffset < inputEnd) {
            finishBlockIfFull();

            int spaceInBlock = getBlockSize() - getBlockPos();
            int inputPartEnd = Math.min(inputEnd, newOffset + spaceInBlock);
            int inputPartLength = inputPartEnd - newOffset;
            getBlockHasher().update(input, newOffset, inputPartLength);

            setBlockPos(getBlockPos() + inputPartLength);
            newOffset += inputPartLength;
        }
    }

    @Override
    protected void engineUpdate(ByteBuffer input) {
        int inputEnd = input.limit();
        while (input.position() < inputEnd) {
            finishBlockIfFull();

            int spaceInBlock = getBlockSize() - getBlockPos();
            int inputPartEnd = Math.min(inputEnd, input.position() + spaceInBlock);
            int inputPartLength = inputPartEnd - input.position();
            input.limit(inputPartEnd);
            getBlockHasher().update(input);

            setBlockPos(getBlockPos() + inputPartLength);
            input.position(inputPartEnd);
        }
    }

    @Override
    protected byte[] engineDigest() {
        finishBlockIfNonEmpty();
        return getOverallHasher().digest();
    }

    @Override
    protected int engineDigest(byte[] buf, int offset, int len)
            throws DigestException {
        finishBlockIfNonEmpty();
        return getOverallHasher().digest(buf, offset, len);
    }

    @Override
    protected void engineReset() {
        getOverallHasher().reset();
        getBlockHasher().reset();
        setBlockPos(0);
    }

    @Override
    public DropBoxContentHasher clone()
            throws CloneNotSupportedException {
        DropBoxContentHasher clone = (DropBoxContentHasher) super.clone();
        clone.setOverallHasher((MessageDigest) clone.getOverallHasher().clone());
        clone.setBlockHasher((MessageDigest) clone.getBlockHasher().clone());
        return clone;
    }

    private void finishBlock() {
        getOverallHasher().update(getBlockHasher().digest());
        setBlockPos(0);
    }

    private void finishBlockIfFull() {
        if (getBlockPos() == getBlockSize()) {
            finishBlock();
        }
    }

    private void finishBlockIfNonEmpty() {
        if (getBlockPos() > 0) {
            finishBlock();
        }
    }

    static MessageDigest newSha256Hasher() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new AssertionError("Couldn't create SHA-256 hasher " + ex);
        }
    }

    public String hex(byte[] data) {
        char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        char[] buf = new char[2 * data.length];
        int i = 0;
        for (byte b : data) {
            buf[i++] = hexDigits[(b & 0xf0) >>> 4];
            buf[i++] = hexDigits[b & 0x0f];
        }
        return new String(buf);
    }

    private MessageDigest getOverallHasher() {
        return overallHasher;
    }

    private void setOverallHasher(MessageDigest overallHasher) {
        this.overallHasher = overallHasher;
    }

    private MessageDigest getBlockHasher() {
        return blockHasher;
    }

    private void setBlockHasher(MessageDigest blockHasher) {
        this.blockHasher = blockHasher;
    }

    private int getBlockPos() {
        return blockPos;
    }

    private void setBlockPos(int blockPos) {
        this.blockPos = blockPos;
    }
}

