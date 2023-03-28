package org.http4k.connect.amazon.kms

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMEncryptedKeyPair
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.ECDSA_SHA_256
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.ECDSA_SHA_384
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.ECDSA_SHA_512
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_256
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_384
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_512
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PSS_SHA_256
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PSS_SHA_384
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PSS_SHA_512
import org.http4k.connect.model.Base64Blob
import java.io.StringReader
import java.security.Signature
import java.security.spec.MGF1ParameterSpec
import java.security.spec.MGF1ParameterSpec.SHA256
import java.security.spec.MGF1ParameterSpec.SHA384
import java.security.spec.MGF1ParameterSpec.SHA512
import java.security.spec.PSSParameterSpec

sealed class KMSSigningAlgorithm(val javaAlgo: String) {
    abstract fun verify(pair: KeyPair, message: Base64Blob, signature: Base64Blob): Boolean

    abstract fun sign(pair: KeyPair, message: Base64Blob): Base64Blob

    protected fun readPrivateKey(pair: KeyPair) = JcaPEMKeyConverter().run {
        val provider = BouncyCastleProvider()
        setProvider(provider)

        val pemObject = PEMParser(StringReader(String(pair.private.decodedBytes()))).readObject()
        getKeyPair(
            when (pemObject) {
                is PEMEncryptedKeyPair -> pemObject.decryptKeyPair(
                    JcePEMDecryptorProviderBuilder().setProvider(provider).build(pair.password.toCharArray())
                )

                else -> pemObject as PEMKeyPair
            }
        ).private
    }

    companion object {
        val KMS_ALGORITHMS = mapOf(
            RSASSA_PSS_SHA_256 to RSA_PSS("SHA256withRSA/PSS", SHA256, 32, "SHA-256"),
            RSASSA_PSS_SHA_384 to RSA_PSS("SHA384withRSA/PSS", SHA384, 48, "SHA-384"),
            RSASSA_PSS_SHA_512 to RSA_PSS("SHA512withRSA/PSS", SHA512, 64, "SHA-512"),
            RSASSA_PKCS1_V1_5_SHA_256 to RSA_PCKS1_V1_5("SHA256withRSA"),
            RSASSA_PKCS1_V1_5_SHA_384 to RSA_PCKS1_V1_5("SHA384withRSA"),
            RSASSA_PKCS1_V1_5_SHA_512 to RSA_PCKS1_V1_5("SHA512withRSA"),
            ECDSA_SHA_256 to ECDSA("SHA256withECDSA"),
            ECDSA_SHA_384 to ECDSA("SHA384withECDSA"),
            ECDSA_SHA_512 to ECDSA("SHA512withECDSA"),
        )
    }
}


class RSA_PSS(
    algo: String,
    private val mgf: MGF1ParameterSpec,
    private val saltLength: Int,
    private val parameterAlgorithm: String
) :
    KMSSigningAlgorithm(algo) {
    override fun verify(pair: KeyPair, message: Base64Blob, signature: Base64Blob) =
        Signature.getInstance(javaAlgo, BouncyCastleProvider()).run {
            val parser = PEMParser(StringReader(String(pair.public.decodedBytes())))
            initVerify(JcaPEMKeyConverter().getPublicKey(parser.readObject() as SubjectPublicKeyInfo))
            update(message.decodedBytes())
            verify(signature.decodedBytes())
        }

    override fun sign(pair: KeyPair, message: Base64Blob) = Base64Blob.encode(
        Signature.getInstance(javaAlgo, BouncyCastleProvider()).run {
            initSign(readPrivateKey(pair))
            setParameter(PSSParameterSpec(parameterAlgorithm, "MGF1", mgf, saltLength, 1))
            update(message.decodedBytes())
            sign()
        })
}

class RSA_PCKS1_V1_5(algo: String) : KMSSigningAlgorithm(algo) {
    override fun verify(pair: KeyPair, message: Base64Blob, signature: Base64Blob) =
        Signature.getInstance(javaAlgo, BouncyCastleProvider()).run {
            val parser = PEMParser(StringReader(String(pair.public.decodedBytes())))
            initVerify(JcaPEMKeyConverter().getPublicKey(parser.readObject() as SubjectPublicKeyInfo))
            update(message.decodedBytes())
            verify(signature.decodedBytes())
        }

    override fun sign(pair: KeyPair, message: Base64Blob) = Base64Blob.encode(
        Signature.getInstance(javaAlgo, BouncyCastleProvider()).run {
            initSign(readPrivateKey(pair))
            update(message.decodedBytes())
            sign()
        })

}

class ECDSA(algo: String) : KMSSigningAlgorithm(algo) {
    override fun verify(pair: KeyPair, message: Base64Blob, signature: Base64Blob) =
        Signature.getInstance(javaAlgo, BouncyCastleProvider()).run {
            val parser = PEMParser(StringReader(String(pair.public.decodedBytes())))
            initVerify(JcaPEMKeyConverter().getPublicKey(parser.readObject() as SubjectPublicKeyInfo))
            update(message.decodedBytes())
            try {
                verify(signature.decodedBytes())
            } catch (e: Exception) {
                false
            }
        }

    override fun sign(pair: KeyPair, message: Base64Blob) = Base64Blob.encode(
        Signature.getInstance(javaAlgo, BouncyCastleProvider()).run {
            initSign(readPrivateKey(pair))
            update(message.decodedBytes())
            sign()
        })

}
