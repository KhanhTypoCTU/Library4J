package ctu.cict.khanhtypo.books;

import org.bson.BsonInt32;
import org.bson.types.ObjectId;

import java.util.Objects;

public final class BookId {
    private final Integer idNum;
    private final ObjectId objectId;

    private BookId(Integer idNum, ObjectId objectId) {
        this.idNum = idNum;
        this.objectId = objectId;
    }

    public static BookId from(Object unknownType, boolean deferred) {
        if (unknownType instanceof ObjectId oid) {
            return new BookId(null, oid);
        } else if (unknownType instanceof Integer integer) {
            return new BookId(integer, null);
        } else if (unknownType instanceof BsonInt32 int32) {
            return new BookId(int32.getValue(), null);
        }
        if (deferred && unknownType == null) return null;
        throw new IllegalArgumentException("Unknown type: " + unknownType);
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

    public Object getAsGenericObject() {
        return this.isObjectId() ? this.objectId : this.idNum;
    }

    @Override
    public int hashCode() {
        return this.isObjectId() ? this.objectId.hashCode() : this.idNum.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BookId bookId)) return false;
        return o == this || Objects.equals(idNum, bookId.idNum) && Objects.equals(objectId, bookId.objectId);
    }
}
