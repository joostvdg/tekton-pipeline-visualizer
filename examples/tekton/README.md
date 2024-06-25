# Tekton Tests

Files for triggering Tekton Pipelines via EventListeners.

These should be send to the EventListener's in the cluster.

## Examples

```shell
kubectl exec -it tmp-shell-grape -- bash
```

```shell
http el-stitches01.her-test.svc.cluster.local:8080 < trig1.json
```

```shell
http el-stitches02.her-test.svc.cluster.local:8080 < trig2.json
```

```shell
http el-stitches03.her-test.svc.cluster.local:8080 < trig3.json
```

```shell
http el-stitches04.her-test.svc.cluster.local:8080 < trig4.json
```

## Match On Result Types?

* 01: `RESULT_TYPE: source-build`
* 02: `RESULT_TYPE: image-build`
* 03: `RESULT_TYPE: image-scan`
