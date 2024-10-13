package org.cosmy.state;

import org.cosmy.ObservableModelRegistryImpl;

import java.io.*;
import java.nio.file.Path;

public class FilePersistedStateManager implements IPersistedStateManager {

    private final File baseDirectory;
    private static FilePersistedStateManager instance;

    public static synchronized IPersistedStateManager getInstance() {
        if (instance == null) {
            synchronized (FilePersistedStateManager.class) {
                if (instance == null) {
                    instance = new FilePersistedStateManager();
                }
            }
        }
        return instance;
    }

    private FilePersistedStateManager() {
        String userHomePath = System.getProperty("user.home");
        System.out.println("user home is set to " + userHomePath);
        baseDirectory = Path.of(userHomePath, ".cosmy").toFile();
        if (!baseDirectory.exists() || !baseDirectory.isDirectory()) {
            baseDirectory.mkdir();
        }
    }

    public static void main(String[] args) {
        FilePersistedStateManager stateManager = new FilePersistedStateManager();
    }

    /**
     * @param object
     */
    @Override
    public void persist(Object object) {
        File file = Path.of(String.valueOf(baseDirectory.toPath()), object.getClass().getCanonicalName()).toFile();
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (ObjectOutputStream objectStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            objectStream.writeObject(object);
            objectStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param
     */
    @Override
    public Object load(Class type) throws IOException, ClassNotFoundException {
        File file = Path.of(String.valueOf(baseDirectory.toPath()), type.getCanonicalName()).toFile();
        if (!file.exists()) {
            System.out.println("State  deosnt exist..");
        }
        try (ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            Object o = objectStream.readObject();
            return o;
        }
    }

    /**
     * @param object
     */
    @Override
    public void reload(Object object) {

    }

    /**
     * @param object
     */
    @Override
    public void refresh(Object object) {

    }
}
