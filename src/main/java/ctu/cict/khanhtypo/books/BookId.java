package ctu.cict.khanhtypo.books;

import org.bson.types.ObjectId;

public final class BookId {
    private final int idNum;
    private final ObjectId objectId;

    private BookId(int idNum, ObjectId objectId) {
        this.idNum = idNum;
        this.objectId = objectId;
    }

    public static BookId from(Object unknownType) {
        if (unknownType instanceof ObjectId oid) {
            return new BookId(-1, oid);
        }
        return new BookId(((int) unknownType), null);
    }

    public boolean isObjectId() {
        return objectId != null;
    }

    public boolean isInt() {
        return !this.isObjectId();
    }

    @Override
    public String toString() {
        return this.isObjectId() ? this.objectId.toString() : Integer.toString(this.idNum);
    }

    public Object getAsObject() {
        return this.isObjectId() ? this.objectId : Integer.valueOf(this.idNum);
    }
}
