package ctu.cict.khanhtypo;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.*;
import ctu.cict.khanhtypo.forms.StartupScreen;
import ctu.cict.khanhtypo.utils.DatabaseUtils;
import ctu.cict.khanhtypo.utils.ResourceUtils;
import ctu.cict.khanhtypo.utils.ScreenUtils;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.*;

public class Main {
    public static final String DATABASE_NAME = "LibrarySystem";//"Restaurant";
    public static boolean IN_DEV = false;

    public static void main(String[] args) {
        IN_DEV = ArrayUtils.contains(args, "devmode");
        if (ScreenUtils.canScreenDisplay()) {
            JFrame frame = new JFrame();
            frame.setIconImage(ResourceUtils.getResourceOrThrow("icon.png",
                    input -> {
                        try {
                            return ImageIO.read(input);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            StartupScreen startupScreen = new StartupScreen();
            frame.setContentPane(startupScreen.getBasePanel());
            ScreenUtils.setFrame(frame);
            ScreenUtils.packFrame();
            frame.setVisible(true);
        } else System.out.println("Screen can not be opened because GraphicsEnvironment is headless.");

        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        try {
            System.out.println("Connecting to local MongoDB service...");
            if (!Iterables.any(client.listDatabaseNames(), name -> name.equals(DATABASE_NAME))) {
                System.out.println("Database " + DATABASE_NAME + " does not exists in local MongoDB, attempt to retrieve...");
                tryRetrieve(client);
            }
            MongoDatabase database = client.getDatabase(DATABASE_NAME);
            DatabaseUtils.setDatabase(database);
        } catch (MongoTimeoutException e) {
            System.out.println("MongoDB Timed Out, please turn on mongodb service and run this program again.");
        }
        if (IN_DEV)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> compressDB(client)));
    }

    private static void tryRetrieve(MongoClient client) {
        try {
            decompressDB(client, Files.newInputStream(Path.of("database.zip"), StandardOpenOption.READ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void decompressDB(MongoClient client, InputStream input) {
        ZipInputStream zipInputStream = new ZipInputStream(input);
        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        try {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                MongoCollection<Document> collection = database.getCollection(entry.getName());
                int collectionByteSize = (int) entry.getSize();
                byte[] data = new byte[collectionByteSize];
                int a = zipInputStream.read(data);
                Preconditions.checkState(a != -1, "EOF reached while reading");
                FastByteArrayOutputStream arr = new FastByteArrayOutputStream();
                InflaterOutputStream outputStream = new InflaterOutputStream(arr);
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();

                arr.close();
                byte[] array = arr.toByteArray();
                ByteBuffer buffer = ByteBuffer.wrap(array);
                System.out.println("Array : " + buffer.capacity());
                int size = buffer.getInt();
                int dataBlockStart = 4 * (size + 1);
                for (int i = 1; i <= size; i++) {
                    int entrySize = buffer.getInt();

                    int prevPos = buffer.position();
                    buffer.position(dataBlockStart);
                    byte[] documentData = new byte[entrySize];
                    buffer.get(documentData);

                    String json = new String(documentData, StandardCharsets.US_ASCII);
                    collection.insertOne(Document.parse(json));

                    dataBlockStart += entrySize;
                    buffer.position(prevPos);
                }

                entry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //DEV TEST
    private static void compressDB(MongoClient client) {
        MongoDatabase database = client.getDatabase("Restaurant");
        ListCollectionNamesIterable documents = database.listCollectionNames();
        MongoCursor<String> iterator = documents.iterator();
        try {
            CRC32 crc32 = new CRC32();
            OutputStream fileOutputStream = Files.newOutputStream(Path.of("database.zip"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            zipOutputStream.setLevel(Deflater.NO_COMPRESSION);
            while (iterator.hasNext()) {
                String collectionName = iterator.next();
                FastByteArrayOutputStream arr = new FastByteArrayOutputStream();
                DeflaterOutputStream deflate = new DeflaterOutputStream(arr, new Deflater(Deflater.BEST_COMPRESSION));
                FindIterable<Document> collection = database.getCollection(collectionName).find();
                byte[] rawData = new byte[0];
                int[] documentByteLengths = new int[0];
                for (Document document : collection) {
                    byte[] data = document.toJson().getBytes(StandardCharsets.US_ASCII);
                    rawData = ArrayUtils.addAll(rawData, data);
                    documentByteLengths = ArrayUtils.add(documentByteLengths, data.length);
                }
                byte[] lengthsData = ArrayUtils.arraycopy(Ints.toByteArray(documentByteLengths.length), 0, new byte[4 * (documentByteLengths.length + 1)], 0, 4);
                int i = 4;
                for (int length : documentByteLengths) {
                    ArrayUtils.arraycopy(Ints.toByteArray(length), 0, lengthsData, i, 4);
                    i += 4;
                }
                byte[] toZip = ArrayUtils.addAll(lengthsData, rawData);
                Files.write(Path.of(collectionName + ".data"), toZip, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                deflate.write(toZip);
                deflate.finish();
                deflate.flush();
                deflate.close();
                arr.close();

                byte[] compressedData = arr.toByteArray();
                ZipEntry entry = new ZipEntry(collectionName);
                entry.setMethod(ZipEntry.STORED);
                entry.setCompressedSize(compressedData.length);
                entry.setSize(compressedData.length);
                crc32.update(compressedData);
                entry.setCrc(crc32.getValue());
                crc32.reset();
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(compressedData);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        iterator.close();
    }
}
