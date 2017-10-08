/**
 * MIT License
 * 
 * Copyright (c) 2017 iMinusMinus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ml.iamwhatiam.ag.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * snowflake java
 * 
 * @author liangming 2017年8月29日 上午9:35:34
 */
public class IdWorker {

    private Logger log                = LoggerFactory.getLogger(IdWorker.class);

    private long   workerId;

    private long   dataCenterId;

    private Object reporter;

    private long   sequence;

    private long   workerIdBits       = 5L;
    private long   datacenterIdBits   = 5L;
    private long   maxWorkerId        = -1L ^ (-1L << workerIdBits);
    private long   maxDatacenterId    = -1L ^ (-1L << datacenterIdBits);
    private long   sequenceBits       = 12L;

    private long   workerIdShift      = sequenceBits;
    private long   datacenterIdShift  = sequenceBits + workerIdBits;
    private long   timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long   sequenceMask       = -1L ^ (-1L << sequenceBits);

    private long   lastTimestamp      = -1L;

    private long   twepoch            = 1288834974657L;

    private String regex              = "([a-zA-Z][a-zA-Z0-9]*)";

    public IdWorker(long workerId, long dataCeneterId, Object reporter, Long sequence) {
        if (sequence == null)
            this.sequence = 0L;
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("worker Id can't be greater than " + maxWorkerId + " or less than 0");
        }
        this.workerId = workerId;
        if (dataCeneterId > maxDatacenterId || dataCeneterId < 0L) {
            throw new IllegalArgumentException(
                    "datacenter Id can't be greater than " + maxDatacenterId + " or less than 0");
        }
        this.reporter = reporter;
        this.dataCenterId = dataCeneterId;
        log.info(
                "worker starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, workerid {}",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);
    }

    private void genCounter(String agent) {
        //        Stats.incr("ids_generated");
        //        Stats.incr(String.format("ids_generated_%s", agent));
    }

    public long getId(String useragent) {
        if (!validUseragent(useragent)) {
            //            throw new InvalidUserAgentError();
        }
        long id = nextId();
        genCounter(useragent);
        //        reporter.report(id, useragent, rand.nextLong);
        return id;
    }

    protected synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            //            throw new InvalidSystemClockException(
            //                    "Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        } else if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << datacenterIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public boolean validUseragent(String useragent) {
        return useragent.matches(regex);
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

}
