package ctu.cict.khanhtypo.forms.component;

import org.bson.BsonString;
import org.bson.BsonValue;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.function.Function;

public interface IBsonRepresentableComponent {
    BsonValue getAsBsonValue();

    Component getComponent();

    static IBsonRepresentableComponent wrap(JTextComponent component) {
        return new IBsonRepresentableComponent() {
            @Override
            public BsonValue getAsBsonValue() {
                return new BsonString(component.getText());
            }

            @Override
            public Component getComponent() {
                return component;
            }
        };
    }

    static <T extends Component> IBsonRepresentableComponent wrap(T component, Function<T, BsonValue> bsonValueFunction) {
        return new IBsonRepresentableComponent() {
            @Override
            public BsonValue getAsBsonValue() {
                return bsonValueFunction.apply(component);
            }

            @Override
            public T getComponent() {
                return component;
            }
        };
    }
}
